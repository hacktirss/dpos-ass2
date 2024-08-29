package com.ass2.volumetrico.puntoventa.pattern;

import com.ass2.volumetrico.puntoventa.data.ComandosVO;

public interface ComandoUpdater {
    public void onPullback(ComandosVO comando);
    public void onDispatching(ComandosVO comando);
    public void onSuccess(ComandosVO comando);
    public void onError(ComandosVO comando);
    public void onTimeout(ComandosVO comando);
}
