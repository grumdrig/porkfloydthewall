import java.io.*;

class BuildWall {
  public static void main(String args[]) {
    if (args.length < 4) {
      System.err.println("Usage: java BuildWall SAVEGAMEPATH X Z RADIUS");
      System.err.println("Duplicate chunk at <X,Z> around <0,0> at RADIUS");
      System.err.println("Don't do this while Minecraft is running");
      System.exit(0);
    }
    final File spath = new File(args[0]);
    int protox = Integer.parseInt(args[1]);
    int protoz = Integer.parseInt(args[2]);
    int radius = Integer.parseInt(args[3]);
    for (int chunk = -radius; chunk < radius; ++chunk) {
      copyChunk(spath, protox, protoz, spath, -radius, chunk);
      copyChunk(spath, protox, protoz, spath,  radius, chunk);
      copyChunk(spath, protox, protoz, spath, chunk,  radius);
      copyChunk(spath, protox, protoz, spath, chunk, -radius);
    }
    RegionFileCache.clear();
  }

  public static void copyChunk(File spath, int sx, int sz,
                               File dpath, int dx, int dz) {
    DataInputStream source =
      RegionFileCache.getChunkDataInputStream(spath, sx, sz);
    if (source == null) {
      System.err.println("ERROR: missing source chunk");
      System.exit(1);
    }
    DataOutputStream dest = 
      RegionFileCache.getChunkDataOutputStream(dpath, dx, dz);
    copyStream(source, dest);
    try {
      source.close();
      dest.close();
    } catch (IOException e) {
      System.err.println("ERROR closing region files");
      System.exit(1);
    }
  }

  private static void copyStream(InputStream input, OutputStream output) {
    try {
      byte[] buffer = new byte[32*1024];
      int bytesRead;
      while ((bytesRead = input.read(buffer, 0, buffer.length)) > 0) {
        output.write(buffer, 0, bytesRead);
      }
    } catch (IOException e) {
      System.err.println("ERROR copying chunk");
      System.exit(1);
    }
  }
}