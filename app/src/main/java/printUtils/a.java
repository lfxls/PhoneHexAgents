package printUtils;

import android.graphics.Bitmap;

final class a
{
  public StyleConfig b = null;
  public String string = null;
  public Bitmap bitmap = null;

  public a(gprinter paramPrinter, String paramString, StyleConfig paramStyleConfig)
  {
    this.string = paramString;
    this.b = paramStyleConfig;
  }

  public a(gprinter paramPrinter, Bitmap paramBitmap, StyleConfig paramStyleConfig)
  {
    this.b = paramStyleConfig;
    this.bitmap = paramBitmap;
  }
}
