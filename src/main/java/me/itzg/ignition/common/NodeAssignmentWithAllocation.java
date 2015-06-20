package me.itzg.ignition.common;

/**
 * @author Geoff Bourne
 * @since 6/20/2015
 */
public class NodeAssignmentWithAllocation extends NodeAssignment {
    private NetAllocation netAllocation;

    public NetAllocation getNetAllocation() {
        return netAllocation;
    }

    public void setNetAllocation(NetAllocation netAllocation) {
        this.netAllocation = netAllocation;
    }
}
