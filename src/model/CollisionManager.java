package model;

public class CollisionManager {
    public enum Operation {
        ADD, REMOVE
    }

    private Movable bMovable;
    private Operation bOperation;

    public CollisionManager(Movable movable, Operation operation) {
        this.bMovable = movable;
        this.bOperation = operation;
    }

    public Movable getMovable() {
        return bMovable;
    }

    public Operation getOperation() {
        return bOperation;
    }
}
