package me.itzg.ignition;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
public class ConfigStatus {
    private boolean machinesPresent;
    private boolean imagesPopulated;

    public boolean isMachinesPresent() {
        return machinesPresent;
    }

    public void setMachinesPresent(boolean machinesPresent) {
        this.machinesPresent = machinesPresent;
    }

    public boolean isImagesPopulated() {
        return imagesPopulated;
    }

    public void setImagesPopulated(boolean imagesPopulated) {
        this.imagesPopulated = imagesPopulated;
    }
}
