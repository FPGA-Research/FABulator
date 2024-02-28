package fabulator.memory;

/**
 *  Classes holding references to the displayed
 *  fabric or related content will implement
 *  this interface - making sure they drop
 *  these references, so they can be garbage
 *  collected
 */
public interface ReferenceHolder {

    void dropReferences();
}
