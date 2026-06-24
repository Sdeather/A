import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * CoordinatePanel: a Swing panel to manage Coordinates.
 * Features:
 * - List of saved coordinates
 * - Add / Edit / Delete
 * - Convert Overworld <-> Nether
 * - Compute distance / horizontal distance between two selected coordinates
 * - Save / Load using CoordinatesStore
 */
public class CoordinatePanel extends JPanel {
    private DefaultListModel<Coordinate> listModel;
    private JList<Coordinate> coordinateJList;

    private JTextField xField;
    private JTextField yField;
    private JTextField zField;
    private JComboBox<Coordinate.Dimension> dimensionCombo;
    private JTextField nameField;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton convertButton;
    private JButton distanceButton;
    private JButton saveButton;
    private JButton loadButton;

    public CoordinatePanel() {
        setLayout(new BorderLayout(8, 8));

        listModel = new DefaultListModel<>();
        coordinateJList = new JList<>(listModel);
        coordinateJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane listScroll = new JScrollPane(coordinateJList);
        listScroll.setPreferredSize(new Dimension(340, 400));

        add(listScroll, BorderLayout.WEST);

        // Right side: form and buttons
        JPanel right = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("X:"), c);
        xField = new JTextField(); c.gridx = 1; c.weightx = 1.0; form.add(xField, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Y:"), c);
        yField = new JTextField(); c.gridx = 1; form.add(yField, c);

        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Z:"), c);
        zField = new JTextField(); c.gridx = 1; form.add(zField, c);

        c.gridx = 0; c.gridy = 3; form.add(new JLabel("Dimension:"), c);
        dimensionCombo = new JComboBox<>(Coordinate.Dimension.values());
        c.gridx = 1; form.add(dimensionCombo, c);

        c.gridx = 0; c.gridy = 4; form.add(new JLabel("Name:"), c);
        nameField = new JTextField(); c.gridx = 1; form.add(nameField, c);

        right.add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 2, 6, 6));
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        convertButton = new JButton("Convert (O<->N)");
        distanceButton = new JButton("Distance");
        saveButton = new JButton("Save");
        loadButton = new JButton("Load");

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(convertButton);
        buttons.add(distanceButton);
        buttons.add(saveButton);
        buttons.add(loadButton);

        right.add(buttons, BorderLayout.CENTER);

        add(right, BorderLayout.CENTER);

        // Hook up actions
        addButton.addActionListener(this::onAdd);
        editButton.addActionListener(this::onEdit);
        deleteButton.addActionListener(e -> onDelete());
        convertButton.addActionListener(e -> onConvert());
        distanceButton.addActionListener(e -> onDistance());
        saveButton.addActionListener(e -> onSave());
        loadButton.addActionListener(e -> onLoad());

        // Load existing coordinates if any
        onLoad();
    }

    private void onAdd(ActionEvent e) {
        try {
            double x = Double.parseDouble(xField.getText().trim());
            double y = Double.parseDouble(yField.getText().trim());
            double z = Double.parseDouble(zField.getText().trim());
            Coordinate.Dimension dim = (Coordinate.Dimension) dimensionCombo.getSelectedItem();
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "Unnamed";

            Coordinate coord = new Coordinate(x, y, z, dim, name);
            listModel.addElement(coord);
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for X, Y, Z.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit(ActionEvent e) {
        int idx = coordinateJList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to edit.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Coordinate selected = listModel.get(idx);
        xField.setText(String.valueOf(selected.getX()));
        yField.setText(String.valueOf(selected.getY()));
        zField.setText(String.valueOf(selected.getZ()));
        dimensionCombo.setSelectedItem(selected.getDimension());
        nameField.setText(selected.getName());

        int result = JOptionPane.showConfirmDialog(this, createEditPanel(), "Edit Coordinate", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double x = Double.parseDouble(xField.getText().trim());
                double y = Double.parseDouble(yField.getText().trim());
                double z = Double.parseDouble(zField.getText().trim());
                Coordinate.Dimension dim = (Coordinate.Dimension) dimensionCombo.getSelectedItem();
                String name = nameField.getText().trim();
                if (name.isEmpty()) name = "Unnamed";
                Coordinate updated = new Coordinate(x, y, z, dim, name);
                listModel.set(idx, updated);
                clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for X, Y, Z.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createEditPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 4, 4));
        p.add(new JLabel("X:")); p.add(xField);
        p.add(new JLabel("Y:")); p.add(yField);
        p.add(new JLabel("Z:")); p.add(zField);
        p.add(new JLabel("Dimension:")); p.add(dimensionCombo);
        p.add(new JLabel("Name:")); p.add(nameField);
        return p;
    }

    private void onDelete() {
        int[] idxs = coordinateJList.getSelectedIndices();
        if (idxs.length == 0) return;
        // remove from highest index to lowest
        for (int i = idxs.length - 1; i >= 0; i--) {
            listModel.remove(idxs[i]);
        }
    }

    private void onConvert() {
        int idx = coordinateJList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Select a coordinate to convert.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Coordinate sel = listModel.get(idx);
        try {
            Coordinate converted;
            if (sel.getDimension() == Coordinate.Dimension.OVERWORLD) {
                converted = sel.toNether();
            } else if (sel.getDimension() == Coordinate.Dimension.NETHER) {
                converted = sel.toOverworld();
            } else {
                JOptionPane.showMessageDialog(this, "Conversion only supported between Overworld and Nether.", "Unsupported", JOptionPane.WARNING_MESSAGE);
                return;
            }
            listModel.set(idx, converted);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Conversion error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDistance() {
        int[] idxs = coordinateJList.getSelectedIndices();
        if (idxs.length != 2) {
            JOptionPane.showMessageDialog(this, "Please select exactly two coordinates to compute distance.", "Selection required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Coordinate a = listModel.get(idxs[0]);
        Coordinate b = listModel.get(idxs[1]);
        try {
            double d3 = a.distanceTo(b);
            double dh = a.horizontalDistanceTo(b);
            String msg = String.format("3D distance: %.3f\nHorizontal distance: %.3f", d3, dh);
            JOptionPane.showMessageDialog(this, msg, "Distance", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dimension mismatch", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave() {
        List<Coordinate> list = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) list.add(listModel.get(i));
        try {
            CoordinatesStore.save(list);
            JOptionPane.showMessageDialog(this, "Saved coordinates.", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLoad() {
        try {
            List<Coordinate> loaded = CoordinatesStore.load();
            listModel.clear();
            for (Coordinate c : loaded) listModel.addElement(c);
        } catch (Exception ex) {
            // If file does not exist this is fine; otherwise show error
            if (!ex.getMessage().contains("No saved file")) {
                JOptionPane.showMessageDialog(this, "Failed to load: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        xField.setText(""); yField.setText(""); zField.setText(""); nameField.setText("");
        dimensionCombo.setSelectedIndex(0);
    }
}
