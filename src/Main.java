import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private static class ButtonTransferHandler extends TransferHandler {
        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JButton button = (JButton) c;
            return new StringSelection(button.getText());
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            // Не ни е нужно нищо, защото не ни трябва да export-ваме
        }
    }


    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int PRODUCT_BUTTON_WIDTH = 150;
    private static final int PRODUCT_BUTTON_HEIGHT = 50;

    private JFrame mainFrame;
    private JPanel basketPanel;
    private double totalPrice = 0.0;

    private JButton payButton;

    public Main() {
        mainFrame = new JFrame("Drag and Drop Products");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainFrame.setLayout(new BorderLayout());

        createProductPanel();
        createBasketPanel();
        createPayButton();

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

    }
    private void createProductPanel() {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        String[] products = {"Waffle", "Cheese", "Meat", "Biscuits", "Bread"};
        for (String product : products) {
            JButton productButton = new JButton(product + ": 2lv");
            productButton.setPreferredSize(new Dimension(PRODUCT_BUTTON_WIDTH, PRODUCT_BUTTON_HEIGHT));

            productButton.setTransferHandler(new ButtonTransferHandler());

            productButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JButton button = (JButton) e.getSource();
                    TransferHandler handle = button.getTransferHandler();
                    handle.exportAsDrag(button, e, TransferHandler.COPY);
                }
            });

            productPanel.add(productButton);
        }

        mainFrame.add(productPanel, BorderLayout.NORTH);
    }

    private void createBasketPanel() {
        basketPanel = new JPanel();
        basketPanel.setPreferredSize(new Dimension(200, WINDOW_HEIGHT));
        basketPanel.setBorder(BorderFactory.createTitledBorder("Basket"));
        basketPanel.setLayout(new BoxLayout(basketPanel, BoxLayout.Y_AXIS));

        // Allow the basket to accept dropped buttons

        JLabel priceLabel = new JLabel("Total Price: 0.0lv", SwingConstants.CENTER);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        basketPanel.add(priceLabel);

        basketPanel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    String
                            data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    String[] parts = data.split(": ");
                    String productName = parts[0];
                    double productPrice = Double.parseDouble(parts[1].replace("lv", "").trim());

                    totalPrice += productPrice; // Add product price to total
                    priceLabel.setText("Total Price: " + String.format("%.2flv", totalPrice));

                    JLabel productLabel = new JLabel(data);
                    basketPanel.add(productLabel);
                    basketPanel.revalidate();
                    basketPanel.repaint();
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
                });

        mainFrame.add(basketPanel, BorderLayout.EAST);
    }

    private void createPayButton() {
        payButton = new JButton("Pay");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame paymentFrame = new JFrame("Payment Details");
                paymentFrame.setSize(300, 200);
                paymentFrame.setLayout(new GridLayout(3, 2, 10, 10));

                JLabel nameLabel = new JLabel("Name:");
                JTextField nameTextField = new JTextField();
                paymentFrame.add(nameLabel);
                paymentFrame.add(nameTextField);

                JLabel cardLabel = new JLabel("Card Number:");
                JTextField cardTextField = new JTextField();
                paymentFrame.add(cardLabel);
                paymentFrame.add(cardTextField);

                JButton submitButton = new JButton("Submit");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String name = nameTextField.getText();
                        String cardNumber = cardTextField.getText();
                        JOptionPane.showMessageDialog(paymentFrame, "Payment processed for " + name);
                        paymentFrame.dispose();
                    }
                });
                paymentFrame.add(new JLabel());
                paymentFrame.add(submitButton);

                paymentFrame.setLocationRelativeTo(mainFrame);
                paymentFrame.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(payButton);
        mainFrame.add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

}
