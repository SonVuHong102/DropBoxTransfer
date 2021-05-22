/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

public class Test1 {
	private static final String ACCESS_TOKEN = "sl.AxO5K6NIGbuikSfbWh484KVeYD00dwU7HkYANeqANUHtUYyM9lNpOgATlFFQdr8AydwJOyY99LWCiKn9LlQ1GvLjknkL2aLV8FrM4TXUXwEtXoO0x57WgeD2wx_kPzFbxcqAK_z57Zw1";

	public static void main(String args[]) throws DbxApiException, DbxException, IOException {
		DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
		
		ListFolderResult result = client.files().listFolder("");
		while (true) {
			for (Metadata metadata : result.getEntries()) {
				System.out.println(metadata.getPathLower());
			}
			if (!result.getHasMore()) {
				break;
			}
			result = client.files().listFolderContinue(result.getCursor());
		}
//		InputStream in = new FileInputStream("C:\\Users\\Admin\\Downloads\\2.txt");
//		FileMetadata metadata = client.files().uploadBuilder("/test.txt").uploadAndFinish(in);
//		in.close();
		DbxDownloader<FileMetadata> downloader = client.files().download("/test.txt");
		FileOutputStream out = new FileOutputStream("test.txt");
		downloader.download(out);
		out.close();
	}
}
