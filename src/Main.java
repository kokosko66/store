import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.desktop.QuitEvent;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import javax.swing.*;


class Price extends JButton {

    double price;

    public Price(String text, double newPrice) {
        super(text);
        this.price = newPrice;
    }

}
public class Main {

    static void Window() {
        //Create window
        JFrame f = new JFrame();

        f.setSize(800, 600);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        JPanel dropPanel = new JPanel();
        dropPanel.setBounds(250, 0, 100, 100);
        dropPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        dropPanel.setLayout(new BoxLayout(dropPanel, BoxLayout.Y_AXIS));
        dropPanel.setTransferHandler(new TransferHandler("text"));
        dropPanel.setName("Basket");
        JLabel label = new JLabel("<html>Basket</html>");

        JLabel priceLabel = new JLabel("<html><center>Price: </center></html>");
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropPanel.add(label);
        dropPanel.add(priceLabel);
        f.add(dropPanel);

        Price[] products = new Price[] {
                new Price("Waffle: 2lv", 2),
                new Price("Cheese: 5.6lv", 5.6),
                new Price("Meat: 12lv", 12),
                new Price("Biscuits: 2.5lv", 2.5),
                new Price("Bread: 2lv", 2),
        };

        products[0].setBounds(0, 50, 100, 50);
        products[1].setBounds(0, 100, 100, 50);
        products[2].setBounds(0, 150, 100, 50);
        products[3].setBounds(0, 200, 100, 50);
        products[4].setBounds(0, 250, 100, 50);

        for(int i = 0; i < products.length; i++) {
            Price product = getPrice(products, i);
            f.add(product);
        }

        final double[] finalPrice = {0};
        final var allProducts = new ArrayList<Price>();

        dropPanel.setTransferHandler(new TransferHandler() {
            public boolean canImport(TransferHandler.TransferSupport support) {
                return true;
            }

            public boolean importData(TransferHandler.TransferSupport support) {
                try {
                    String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);

                    for (Price product : products) {
                        if (product.getText().equals(data)) {
                            product.setVisible(false);
                            label.setText(product.getText());
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    SwingUtilities.invokeLater(() -> {
                                        product.setVisible(true);
                                        label.setText("Basket");
                                        finalPrice[0] += product.price;
                                        allProducts.add(product);
                                        priceLabel.setText("<html><center>Price " + finalPrice[0] + "lv</center></html>");
                                        JButton purchase = new JButton("Purchase");
                                        purchase.setBounds(400, 0, 100, 100);
                                        f.add(purchase);

                                        JButton finishTransaction = new JButton("Pay");


                                        MouseListener mouseListener = new MouseAdapter() {
                                            @Override
                                            public void mouseClicked(MouseEvent e) {
                                                JFrame subFrame = new JFrame();

                                                JPanel paymentPanel = new JPanel();

                                                paymentPanel.setSize(300, 550);

                                                subFrame.setSize(350, 600);
                                                subFrame.setVisible(true);

                                                var finalArrayList = new ArrayList<>(allProducts);

                                                for (Price values : finalArrayList) {
                                                    paymentPanel.add(values);
                                                }

                                                finishTransaction.setSize(100, 50);

                                                MouseListener finishListener = new MouseAdapter() {
                                                    @Override
                                                    public void mouseClicked(MouseEvent e) {
                                                        f.dispose();
                                                        subFrame.dispose();
                                                    }
                                                };

                                                finishTransaction.addMouseListener(finishListener);
                                                paymentPanel.add(finishTransaction);
                                                subFrame.add(paymentPanel);
                                            }


                                        };



                                        purchase.addMouseListener(mouseListener);

                                    });
                                }
                            }, 1500);
                            break;
                        }
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }

                return true;
            }
        });

        //Operations for window
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        dropPanel.setLayout(null);
    }

    private static Price getPrice(Price[] products, int i) {
        Price product = products[i];
        product.setBounds(0, 50 + (i * 50), 100, 50);
        product.setTransferHandler(new SimpleTransferHandler());

        product.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                TransferHandler handler = button.getTransferHandler();
                if (handler != null) {
                    handler.exportAsDrag(button, e, TransferHandler.COPY);
                }
            }
        });
        return product;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::Window);
    }
}