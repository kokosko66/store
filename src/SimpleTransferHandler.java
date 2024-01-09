import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

class SimpleTransferHandler extends TransferHandler {
    @Override
    protected Transferable createTransferable(JComponent c) {
        return new StringSelection(((JButton) c).getText());
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        JButton button = (JButton) comp;
        final BufferedImage image = new BufferedImage(button.getWidth(), button.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        button.paint(g);
        g.dispose();

        setDragImage(image);

        super.exportAsDrag(comp, e, action);
    }
}