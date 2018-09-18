package com.hb.cultivos.cultivos;

public class SpinnerItem {
        public Integer id;
        public String string;

        public SpinnerItem(Integer id, String string) {
            this.string = string;
            this.id = id;
        }

        @Override
        public String toString() {
            return string;
        }
}
