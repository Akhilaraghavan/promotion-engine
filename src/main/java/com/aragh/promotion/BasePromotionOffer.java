package com.aragh.promotion;

public abstract class BasePromotionOffer implements PromotionOffer {

    protected boolean isEnabled;
    protected final int id;

    protected BasePromotionOffer(int id, boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.id = id + Thread.currentThread().getClass().hashCode();
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public int getId() {
        return id;
    }
}
