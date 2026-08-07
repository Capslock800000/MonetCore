package com.monettheme.api;

interface IMonetColorService {
    Bundle generateThemeFromWallpaper(boolean darkTheme);
    Bundle generateThemeFromColor(int seedColor, boolean darkTheme);
    Bundle getCurrentPalette();
    int getServiceVersion();
}
