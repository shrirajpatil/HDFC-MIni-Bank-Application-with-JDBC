package com.hdfc.minibank;

import com.hdfc.minibank.Entities.*;
import com.hdfc.minibank.dao.AccountDAO;
import com.hdfc.minibank.dao.CustomerDAO;
import com.hdfc.minibank.dao.TransactionDAO;
import com.hdfc.minibank.enums.TransactionType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.hdfc.minibank.Exception.*;
import com.hdfc.minibank.util.DBConnectionUtil;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Map<String, Customer> customers = new HashMap<>();
    private static final Map<String, Account> accounts = new HashMap<>();
    private static final List<Transaction> transactions= new ArrayList<>();

    private static final Scanner scanner = new Scanner(System.in);

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args){
        System.out.println("Welcome to HDFC banking app");
        try {
            while(true){
                showMainMenu();
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println("Error :"+e.getMessage());
        }finally{
            scanner.close();
        }
    }

    private static void showMainMenu() {
        System.out.println("\n == MAIN MENU ==");
        System.out.println("1.Register new customer");
        System.out.println("2. Create account");
        System.out.println("3. Perform Transaction");
        System.out.println("4. view account details");
        System.out.println("5. view transaction history");
        System.out.println("6. Exit");

        System.out.println("Enter your choice ");
        int choice = getInput();

        switch (choice){
            case 1:
                registerCustomer();
                break;
            case 2:
                createAccount();
                break;
            case 3:
                performTransaction();
                break;
            case 4:
                viewAccountDetails();
                break;

            case 5:
                viewTransactionHistory();
                break;
        }

    }




    private static void performTransaction() {
        System.out.println("\n ===perform transaction===");
        System.out.println("1. deposit");
        System.out.println("2. withdraw");
        System.out.println(("3. transfer"));
        System.out.println("select transaction type");

        int transactionChoice = getInput();
        switch(transactionChoice){
            case 1:
                performDeposit();
                break;
            case 2:
                performWithdraw();
                break;
            case 3:
                performTransfer();
                break;
            default:
                System.out.println("Invalid transaction");

        }
    }




    private static void performDeposit() {
        System.out.println("Enter account number");
        String accountNo = scanner.nextLine().trim();

        System.out.println("Enter deposit amount");
        String amountStr = scanner.nextLine().trim();

        try (Connection conn = DBConnectionUtil.getConnection()) {
            BigDecimal amount = new BigDecimal(amountStr);

            conn.setAutoCommit(false); // start transaction
            AccountDAO accountDAO = new AccountDAO(conn);
            TransactionDAO txnDAO = new TransactionDAO(conn);

            Account account = accountDAO.getAccountByNumber(accountNo);
            if (account == null) {
                System.out.println("Account not found");
                return;
            }

            // update balance
            account.setBalance(account.getBalance().add(amount));
            accountDAO.updateBalance(account);

            // log transaction
            String transactionId = generateTransactionId();
            Transaction transaction = new Transaction(transactionId, accountNo,
                    TransactionType.DEPOSIT, amount, LocalDateTime.now());
            txnDAO.saveTransaction(transaction);

            conn.commit(); // ✅ commit changes
            System.out.println("Deposit successful! New balance: " + account.getBalance());

        } catch (Exception e) {
            System.out.println("Error during deposit: " + e.getMessage());
        }
    }

    private static String generateTransactionId(){
        return "HDFC_TXN" + System.currentTimeMillis();
    }


    private static void performWithdraw() {
        System.out.println("Enter account number");
        String accountNo = scanner.nextLine().trim();

        System.out.println("Enter withdrawal amount");
        String amountStr = scanner.nextLine().trim();

        try (Connection conn = DBConnectionUtil.getConnection()) {
            BigDecimal amount = new BigDecimal(amountStr);

            conn.setAutoCommit(false); // start transaction
            AccountDAO accountDAO = new AccountDAO(conn);
            TransactionDAO txnDAO = new TransactionDAO(conn);

            Account account = accountDAO.getAccountByNumber(accountNo);
            if (account == null) {
                System.out.println("Account not found");
                return;
            }

            if (account.getBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient balance.");
                return;
            }

            // update balance
            account.setBalance(account.getBalance().subtract(amount));
            accountDAO.updateBalance(account);

            // log transaction
            String transactionId = generateTransactionId();
            Transaction transaction = new Transaction(transactionId, accountNo,
                    TransactionType.WITHDRAWL, amount, LocalDateTime.now());
            txnDAO.saveTransaction(transaction);

            conn.commit(); // ✅ commit changes
            System.out.println("Withdrawal successful! New balance: " + account.getBalance());

        } catch (Exception e) {
            System.out.println("Error during withdrawal: " + e.getMessage());
        }
    }


    private static void performTransfer() {
        System.out.println("Enter source account number:");
        String fromAccountNo = scanner.nextLine().trim();

        System.out.println("Enter destination account number:");
        String toAccountNo = scanner.nextLine().trim();

        if (fromAccountNo.equals(toAccountNo)) {
            System.out.println("Cannot transfer to the same account.");
            return;
        }

        System.out.println("Enter amount to transfer:");
        String amountStr = scanner.nextLine().trim();

        try {
            BigDecimal amount = new BigDecimal(amountStr);

            Connection conn = null; // ✅ Declare outside the try block
            try {
                conn = DBConnectionUtil.getConnection();
                conn.setAutoCommit(false); // Begin transaction

                AccountDAO accountDAO = new AccountDAO(conn);
                TransactionDAO txnDAO = new TransactionDAO(conn);

                Account fromAccount = accountDAO.getAccountByNumber(fromAccountNo);
                Account toAccount = accountDAO.getAccountByNumber(toAccountNo);

                if (fromAccount == null || toAccount == null) {
                    System.out.println("One or both accounts not found.");
                    conn.rollback();
                    return;
                }

                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    System.out.println("Insufficient balance.");
                    conn.rollback();
                    return;
                }

                // Update balances
                fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                toAccount.setBalance(toAccount.getBalance().add(amount));

                accountDAO.updateBalance(fromAccount);
                accountDAO.updateBalance(toAccount);

                // Log transaction
                String txnId = generateTransactionId();
                Transaction txn = new Transaction(txnId, fromAccountNo, TransactionType.TRANSFER, amount, LocalDateTime.now());
                txnDAO.saveTransaction(txn);

                conn.commit(); // ✅ Commit the transaction
                System.out.println("Transfer successful.");

            } catch (Exception e) {
                if (conn != null) conn.rollback(); // ✅ Rollback on error
                System.out.println("Error during transfer: " + e.getMessage());
            } finally {
                if (conn != null) conn.close(); // ✅ Always close connection
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    private static void viewAccountDetails() {
        System.out.println("Account details");
        System.out.println("Enter account number");

        String accountNo = scanner.nextLine().trim();
        Account account = accounts.get(accountNo);
        if(account==null){
            System.out.println("account not found");
            return;
        }

        System.out.println(account.toString());
        if(account instanceof SavingsAccount){
            System.out.println("Savings account");
        }else{
            System.out.println("Current Account");
        }
    }


    private static void viewTransactionHistory() {
        System.out.println("\n ---------- Transaction History --------");
        System.out.println("Enter account no: ");
        String accountNo = scanner.nextLine().trim();

        try (Connection conn = DBConnectionUtil.getConnection()) {
            TransactionDAO txnDAO = new TransactionDAO(conn);
            List<Transaction> accountTransactions = txnDAO.getTransactionsByAccount(accountNo);

            if (accountTransactions.isEmpty()) {
                System.out.println("No Transaction found!!!");
                return;
            }

            // Print sorted transactions
            accountTransactions.stream()
                    .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                    .forEach(System.out::println);

            // Summary
            Map<TransactionType, Long> transactionSummary = accountTransactions.stream()
                    .collect(Collectors.groupingBy(Transaction::getType, Collectors.counting()));

            System.out.println("\n ---------- Transaction Summary --------");
            transactionSummary.forEach((type, count) ->
                    System.out.println(type.getDisplayName() + " : " + count + " Transaction"));

        } catch (Exception e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
    }

    private static void createAccount() {
        System.out.println("Creating new account");
        System.out.println("Please enter customer id");
        String customerId = scanner.nextLine().trim();

        Customer customer = customers.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found");
            return;
        }

        System.out.println("Choose account type");
        System.out.println("1. Savings Account (6% interest rate and min balance of Rs1000)");
        System.out.println("2. Current Account (4% interest rate and no min balance)");
        int typeChoice = getInput();

        System.out.println("Enter initial balance");
        String balance = scanner.nextLine().trim();

        try (Connection conn = DBConnectionUtil.getConnection()) {
            BigDecimal initialBalance = new BigDecimal(balance);
            String accountNo = generateAccountNo();

            Account account;
            switch (typeChoice) {
                case 1:
                    account = new SavingsAccount(accountNo, customerId, initialBalance);
                    break;
                case 2:
                    account = new CurrentAccount(accountNo, customerId, initialBalance);
                    break;
                default:
                    System.out.println("Invalid account type");
                    return;
            }

            // ✅ Save to database
            AccountDAO accountDAO = new AccountDAO(conn);
            accountDAO.createAccount(account);

            // (Optional) also keep it in memory if you’re still using accounts map
            accounts.put(accountNo, account);

            System.out.println("Congratulations!! Account created. Account No: " + accountNo);

        } catch (NumberFormatException e) {
            System.out.println("Invalid balance amount");
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }


    private static String generateAccountNo() {
        return String.format("%10d", System.currentTimeMillis()%10000000000L);
    }

//
private static void registerCustomer() {
    System.out.println("---- Customer Registration ---");
    System.out.println("Enter customer id");
    String customerId = scanner.nextLine().trim();

    if (customers.containsKey(customerId)) {
        System.out.println("Customer already exists");
        return;
    }

    System.out.println("Enter name");
    String name = scanner.nextLine().trim();

    String email;
    while (true) {
        System.out.println("Enter email");
        email = scanner.nextLine().trim();
        if (email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) break;
        else System.out.println("Invalid email format.");
    }

    String phone;
    while (true) {
        System.out.println("Enter phone (10 digits)");
        phone = scanner.nextLine().trim();
        if (phone.matches("^\\d{10}$")) break;
        else System.out.println("Invalid phone number.");
    }

    System.out.println("Enter dob (yyyy-MM-dd)");
    String dobStr = scanner.nextLine().trim();

    try {
        LocalDate dateOfBirth = LocalDate.parse(dobStr, dateFormatter);

        Customer customer = new Customer(customerId, name, email, phone, dateOfBirth.toString());

        try (Connection conn = DBConnectionUtil.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAO(conn);
            customerDAO.createCustomer(customer);
        }

        customers.put(customerId, customer);
        System.out.println("Customer registered successfully.");

    } catch (Exception e) {
        System.out.println("Error registering customer: " + e.getMessage());
        e.printStackTrace();
    }
}




    private static int getInput(){
        while(true){
            try{
                return Integer.parseInt(scanner.nextLine().trim());
            }catch(NumberFormatException e){
                System.out.println("please enter valid number");

            }
        }
    }

}