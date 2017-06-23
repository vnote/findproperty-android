package com.cetnaline.findproperty.utils.glide;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.cetnaline.findproperty.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public class GlideLoad {

	public static DrawableRequestBuilder<String> init(Activity activity) {
		return Glide.with(activity)
				.fromString()
				.skipMemoryCache(Build.VERSION.SDK_INT < 21)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.crossFade()
				.clone();
	}

	public static DrawableRequestBuilder<String> init(Fragment fragment) {
		return Glide.with(fragment)
				.fromString()
				.skipMemoryCache(Build.VERSION.SDK_INT < 21)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.crossFade()
				.clone();
	}

	public static class Builder{
		private int placeholder = 0;
		private int error = 0;
		private boolean fitCenter;
		private final DrawableRequestBuilder<String> requestBuilder;
		private final String url;
		private ImageView imageView;
		private BitmapTransformation transformation;

		public Builder(DrawableRequestBuilder<String> requestBuilder, String url) {
			this.requestBuilder = requestBuilder;
			this.url = url;
			placeHolder(R.drawable.ic_default_est_estate);
			error(R.drawable.ic_default_est_estate);
		}

		public Builder(DrawableRequestBuilder<String> requestBuilder, String url, int placeholderId, int errorId){
			this.requestBuilder = requestBuilder;
			this.url = url;
			placeHolder(placeholderId);
			error(errorId);
		}

		/**
		 * 启用fitCenter,默认centerCrop
		 *
		 * @return Builder
		 */
		public Builder fitCenter() {
			fitCenter = true;
			return this;
		}

		public Builder into(ImageView imageView) {
			this.imageView = imageView;
			return this;
		}

		public Builder placeHolder(@DrawableRes int placeholderId) {
			this.placeholder = placeholderId;
			return this;
		}

		public Builder error(@DrawableRes int error) {
			this.error = error;
			return this;
		}

		public Builder transformation(BitmapTransformation transformation){
			this.transformation=transformation;
			return this;
		}

	}

	public static void load(Builder builder){
		if (builder.fitCenter) {
			builder.requestBuilder
					.load(builder.url)
					.dontAnimate()
					.placeholder(builder.placeholder)
					.error(builder.error)
					.listener(new LogListener<>())
					.fitCenter()
					.into(builder.imageView);
		}else {
			builder.requestBuilder
					.load(builder.url)
					.dontAnimate()
					.placeholder(builder.placeholder)
					.error(builder.error)
					.listener(new LogListener<>())
					.centerCrop()
					.into(builder.imageView);
		}
	}

	public static boolean downLoadImage(Activity context, File file, String url){
		try {
			file = Glide.with(context)
					.load(url)
					.downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
					.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}finally {
			if (file!=null){
				return true;
			}
		}
		return false;
	}

	public static void loadResource(String url, @NonNull ImageView image) {
		DisplayMetrics metrics = image.getResources().getDisplayMetrics();
		final int displayWidth = metrics.widthPixels;
		final int displayHeight = metrics.heightPixels;

		Glide.with(image.getContext())
				.load(url)
				.asBitmap()
				.dontAnimate()
				.placeholder(R.drawable.ic_default_est_estate)
				.error(R.drawable.ic_default_est_estate)
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.into(new BitmapImageViewTarget(image) {
					@Override
					public void getSize(final SizeReadyCallback cb) {
						// We don't want to load very big images on devices with small screens.
						// This will help Glide correctly choose images scale when reading them.
						super.getSize(new SizeReadyCallback() {
							@Override
							public void onSizeReady(int width, int height) {
								cb.onSizeReady(displayWidth / 2, displayHeight / 2);
							}
						});
					}
				});
	}

	/**
	 * 初始化圆形加载
	 *
	 * @return
	 */
	public static BitmapTypeRequest<String> initRound(Activity activity) {
		return Glide
				.with(activity)
				.from(String.class)
				.asBitmap();
	}

	/**
	 * 初始化圆形加载
	 *
	 * @return
	 */
	public static BitmapTypeRequest<String> initRound(Fragment fragment) {
		return Glide
				.with(fragment)
				.from(String.class)
				.asBitmap();
	}

	public interface MyDataModel {
		String buildUrl(int width, int height);
	}

	public class MyUrlLoader extends BaseGlideUrlLoader<MyDataModel> {

		public MyUrlLoader(Context context) {
			super(context);
		}

		@Override
		protected String getUrl(MyDataModel model, int width, int height) {
			// Construct the url for the correct size here.
			return model.buildUrl(width, height);
		}
	}

}
