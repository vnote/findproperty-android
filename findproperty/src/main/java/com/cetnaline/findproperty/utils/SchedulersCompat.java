package com.cetnaline.findproperty.utils;

import com.cetnaline.findproperty.api.service.RegionPostsApi;
import com.cetnaline.findproperty.api.bean.ApiResponse;
import com.cetnaline.findproperty.api.bean.AppAdBo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by fanxl2 on 2016/7/25.
 */
public class SchedulersCompat {

	private final static Observable.Transformer ioTransformer = o -> ((Observable)o).subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread());

	public static <T> Observable.Transformer<T, T> applyIoSchedulers(){
		return (Observable.Transformer<T, T>) ioTransformer;
	}

	public static <T> Observable.Transformer<T, T> test(){
		return new Observable.Transformer<T, T>() {
			@Override
			public Observable<T> call(Observable<T> tObservable) {
				return tObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
			}
		};
	}


	public static <T> Observable.Transformer<T, T> retry(RegionPostsApi api){
		return new Observable.Transformer<T, T>() {
			@Override
			public Observable<T> call(Observable<T> tObservable) {

				return tObservable.retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
					@Override
					public Observable<?> call(Observable<? extends Throwable> observable) {

						return observable.flatMap(new Func1<Throwable, Observable<?>>() {
							@Override
							public Observable<?> call(Throwable throwable) {

								if (throwable instanceof NullPointerException){
									api.getAppAdvert().doOnNext(new Action1<ApiResponse<AppAdBo>>() {
										@Override
										public void call(ApiResponse<AppAdBo> appAdBoApiResponse) {

										}
									});
								}
								return Observable.error(throwable);
							}
						});
					}
				});
			}
		};
	}

//	private final static Observable.Transformer ioformer = new Observable.Transformer() {
//		@Override
//		public Object call(Object o) {
//			return ((Observable)o).subscribeOn(Schedulers.io())
//					.observeOn(AndroidSchedulers.mainThread());
//		}
//	};

}
