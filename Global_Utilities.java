package com.example.myapplicationtest1;

public class Global_Utilities {
    Global_Utilities(){}

    public static class Iterator {
        private int iterator_nb;

        public int getIterator_nb() {
            return iterator_nb;
        }

        public int increase()
        {
        this.iterator_nb++;

        return this.iterator_nb;

        }

        Iterator()
        {this.iterator_nb=0;}
    }
}
