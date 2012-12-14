package spritecrop;

import pwnee.fileio.*;
import java.io.Serializable;
import java.util.HashMap;

/** A saveable/loadable HashMap of persistent variables used by YarHar. */
public class SpriteCropConfig implements Serializable {
  public static String path = "spritecrop.dat";
  
  public HashMap<String, String> vars = new HashMap<String,String>();
  
  public SpriteCropConfig() {
    
  }
  
  public static SpriteCropConfig load() {
    ObjectFileIO ofio = new ObjectFileIO();
    Object obj = ofio.loadObject(SpriteCropConfig.path);
    if(obj != null && obj instanceof SpriteCropConfig)
      return (SpriteCropConfig) obj;
    else {
      SpriteCropConfig config = new SpriteCropConfig();
      config.setDefaults();
      return config;
    }
  }
  
  
  public void save() {
    ObjectFileIO ofio = new ObjectFileIO();
    ofio.saveObject(this,SpriteCropConfig.path);
  }
  
  
  public void setDefaults() {
    vars.put("lastOpen", ".");
  }
  
}
