package net.omar.tutorial.Vaults;

import net.omar.tutorial.Data.Indexes;

public class MyPV extends InventoryEntry {
    public static final String PV1 = "PV1";
    public static final String PV2 = "PV2";
    public MyPV(String name) {
        super(name);
    }

    @Override
    public void update(String operation) {
        VaultsStateManager.updateInventoryState(name, operation, Indexes.PV.PV, this);
        VaultsStateManager.Inventory(MyInventory.NAME).updateFromPV();
    }
}
