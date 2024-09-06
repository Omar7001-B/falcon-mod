package net.omar.tutorial.last;

import net.omar.tutorial.indexes.Indexes;

public class MyPV extends InventoryEntry {
    public static final String PV1 = "PV1";
    public static final String PV2 = "PV2";
    public MyPV(String name) {
        super(name);
    }

    @Override
    public void update(String operation) {
        InventorySaver.updateInventoryState(name, operation, Indexes.PV.PV, this);
        InventorySaver.Inventory(MyInventory.NAME).updateFromPV();
    }
}
