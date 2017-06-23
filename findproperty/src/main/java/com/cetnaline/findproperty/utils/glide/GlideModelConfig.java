package com.cetnaline.findproperty.utils.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

/**
 * Glide全局配置
 * Created by fanxl2 on 2016/8/1.
 */
public class GlideModelConfig implements GlideModule {

	int diskSize = 1024 * 1024 * 100;
	int memorySize = (int) (Runtime.getRuntime().maxMemory()) / 8;  // 取1/8最大内存作为最大缓存

	@Override
	public void applyOptions(Context context, GlideBuilder builder) {

		MemorySizeCalculator memorySizeCalculator = new MemorySizeCalculator(context);

		//定义缓存大小和位置
		builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskSize));  //内存中
		builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, 100 * 1024 * 1024)); //SD卡中
		//定义内存和图片池的大小
		builder.setMemoryCache(new LruResourceCache(memorySizeCalculator.getMemoryCacheSize() * 10 / 100));
		builder.setBitmapPool(new LruBitmapPool(memorySizeCalculator.getBitmapPoolSize() * 10 / 100));

	}

	@Override
	public void registerComponents(Context context, Glide glide) {

	}
}
