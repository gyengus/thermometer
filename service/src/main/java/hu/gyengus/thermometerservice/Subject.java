package hu.gyengus.thermometerservice;

public interface Subject {
    void setObserver(final Observer observer);
    void notifyObserver(final String message);
}
