package com.plusdesignstudia.moneytimer;

/**
 * Created by kirill on 18.06.15.
 */
public class MoneyAndTime {
    public float money;
    public long time;

    public MoneyAndTime(float money, long time){
        this.money= money;
        this.time = time;
    }
    public MoneyAndTime(){
        this.money=(float)0;
        this.time=0;
    }

    @Override
    public String toString() {
         return "money = "+money +"\ntime = "+ time;
    }
}
