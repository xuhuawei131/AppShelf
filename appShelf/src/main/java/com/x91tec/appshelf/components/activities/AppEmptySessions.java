package com.x91tec.appshelf.components.activities;

import com.x91tec.appshelf.ui.MultiStateLayout;

/**
 * Created by oeager on 16-4-28.
 */
public final class AppEmptySessions {

    private AppEmptySessions(){
        throw new UnsupportedOperationException("can not instance class{"+getClass().getName()+"}");
    }

    private static class StateControllerHolder {
        final static MultiStateLayout.StateController IMPL = new MultiStateLayout.StateController() {
            @Override
            public void showLoading(boolean animate) {

            }

            @Override
            public void showContent(boolean animate) {

            }

            @Override
            public void showEmpty(boolean animate) {

            }

            @Override
            public void showError(boolean animate) {

            }

            @Override
            public void showState(int state, boolean animate) {

            }
        };
    }


    public static MultiStateLayout.StateController fromEmptyNullable(MultiStateLayout.StateController controller) {
        if (controller != null) {
            return controller;
        }
        return StateControllerHolder.IMPL;
    }
}
