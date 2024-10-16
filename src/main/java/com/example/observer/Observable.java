package com.example.observer;
/**
 * interface observable pour les classes qui ont besoin d'etre notifier des changement du model
 */
public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}