package com.oasis.atum.base.infrastructure.util;

import lombok.Cleanup;
import lombok.val;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 文件工具
 */
public final class FileUtil
{
	/**
	 * 异步读文件
	 * @param filename
	 * @return
	 */
	public static String read(final String filename)
	{
		try
		{
			@Cleanup
			val raf = new RandomAccessFile(filename, "r");
			@Cleanup
			val fc = raf.getChannel();
			val buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

			buffer.load();
			val content = StandardCharsets.UTF_8.decode(buffer).toString();
			buffer.clear();

			return content;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
