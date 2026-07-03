package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());

            userRepository.save(sender);
            userRepository.save(recipient);

            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
            transactionRepository.save(transactionRecord);
        }
    }
}
