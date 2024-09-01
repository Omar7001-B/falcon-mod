package net.omar.tutorial.last;

import net.omar.tutorial.indexes.Indexes;

public class MyPV extends InventoryEntry {
    public MyPV(String name) {
        super(name);
    }

    @Override
    public void update(String operation) {
        InventorySaver.updateInventoryState(name, operation, Indexes.PV.PV, this);
        InventorySaver.Inventory("Inventory").updateFromPV();
    }
}
