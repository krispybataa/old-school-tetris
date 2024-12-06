package model;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class GameOperationsList extends LinkedList{
    private ReentrantLock lock;
    public GameOperationsList(){
        this.lock = new ReentrantLock();
    }

    public void enqueue(Movable move, CollisionManager.Operation operation){
        try{
            lock.lock();
            addLast(new CollisionManager(move, operation));
        }finally {
            lock.unlock();
        }
    }

    public CollisionManager dequeue(){
        try{
            lock.lock();
            return(CollisionManager) super.removeFirst();
        }finally {
            lock.unlock();
        }
    }
}
