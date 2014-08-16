package org.engine.build;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class BuildUtils {

	public static void main(String[] args) {

		if (args.length > 0) {
			
			if (args[0].startsWith("update-lib")) {
				
				try {
					
					System.out.print("Updating libraries in /libs...");
					int i = 0;
					System.out.print(" " + i + "/8");
					FileUtils.copyURLToFile(new URL("http://libgdx.badlogicgames.com/nightlies/dist/gdx.jar"), new File("libs/gdx.jar"));
					i++;
					System.out.print(" " + i + "/8");
					FileUtils.copyURLToFile(new URL("http://libgdx.badlogicgames.com/nightlies/dist/gdx-natives.jar"), new File("libs/gdx-natives.jar"));
					i++;
					System.out.print(" " + i + "/8");
					FileUtils.copyURLToFile(new URL("http://libgdx.badlogicgames.com/nightlies/dist/sources/gdx-sources.jar"), new File("libs/gdx-sources.jar"));
					i++;
					System.out.print(" " + i + "/8");
					FileUtils.copyURLToFile(new URL("http://libgdx.badlogicgames.com/nightlies/dist/gdx-backend-lwjgl.jar"), new File("libs/gdx-backend-lwjgl.jar"));
					i++;
					System.out.print(" " + i + "/8");
					FileUtils.copyURLToFile(new URL("http://libgdx.badlogicgames.com/nightlies/dist/gdx-backend-lwjgl-natives.jar"), new File("libs/gdx-backend-lwjgl-natives.jar"));
					i++;
					System.out.print(" " + i + "/8");
					FileUtils.copyURLToFile(new URL("http://libgdx.badlogicgames.com/nightlies/dist/sources/gdx-backend-lwjgl-sources.jar"), new File("libs/gdx-backend-lwjgl-sources.jar"));
					i++;
					System.out.print(" " + i + "/8");
					FileUtils.copyURLToFile(new URL("http://libgdx.badlogicgames.com/nightlies/dist/extensions/gdx-tools/sources/gdx-tools-sources.jar"), new File("libs/gdx-tools-sources.jar"));
					i++;
					System.out.print(" " + i + "/8");
					FileUtils.copyURLToFile(new URL("http://libgdx.badlogicgames.com/nightlies/dist/extensions/gdx-tools/gdx-tools.jar"), new File("libs/gdx-tools.jar"));
					System.out.println(" 8/8 done");

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else{
			
			System.out.println(" --- TABLEENGINE BUILD TOOLS --- \n\n"
					+ "update-lib = download libraries from nightly build\n"
					+ "build      = build jar file");
		}
	}
}
