package com.cetnaline.findproperty.utils;

import com.cetnaline.findproperty.api.bean.ApiResponse;
import com.cetnaline.findproperty.api.ServerException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by fanxl2 on 2016/7/25.
 */
public class RxResultHelper {

	public static <T> Observable.Transformer<ApiResponse<T>, T> handleResult(){
		return new Observable.Transformer<ApiResponse<T>, T>() {
			@Override
			public Observable<T> call(Observable<ApiResponse<T>> apiResponseObservable) {

				return apiResponseObservable.flatMap(
						new Func1<ApiResponse<T>, Observable<T>>() {
							@Override
							public Observable<T> call(ApiResponse<T> tApiResponse) {
								if (tApiResponse==null){
									return Observable.empty();
								}else if (tApiResponse.isSuccess()){
									return createData(tApiResponse.getResult());
								}else{
									return Observable.error(new ServerException(tApiResponse.getMessage()+""));
								}
							}
						});
			}
		};
	}

	public static <T> Observable.Transformer<ApiResponse<T>, T> hand(){

		return apiResponseObservable -> {

			return apiResponseObservable.flatMap(new Func1<ApiResponse<T>, Observable<T>>() {
				@Override
				public Observable<T> call(ApiResponse<T> tApiResponse) {

					return null;
				}
			});
		};
	}

	public static <T> Observable.Transformer<ApiResponse<T>, T> handleNoErrorResult(){
		return new Observable.Transformer<ApiResponse<T>, T>() {
			@Override
			public Observable<T> call(Observable<ApiResponse<T>> apiResponseObservable) {

				return apiResponseObservable.flatMap(
						new Func1<ApiResponse<T>, Observable<T>>() {
							@Override
							public Observable<T> call(ApiResponse<T> tApiResponse) {
								if (tApiResponse==null){
									return Observable.empty();
								}else if (tApiResponse.isSuccess()){
									return createData(tApiResponse.getResult());
								}else if (tApiResponse.getResultNo()==-1){
									return createData(tApiResponse.getResult());
								}else{
									return Observable.error(new ServerException(tApiResponse.getMessage()+""));
								}
							}
						});
			}
		};
	}

	private static <T> Observable<T> createData(T result) {
		return Observable.create(new Observable.OnSubscribe<T>() {
			@Override
			public void call(Subscriber<? super T> subscriber) {
				try {
					subscriber.onNext(result);
					subscriber.onCompleted();
				}catch (Exception e){
					subscriber.onError(e);
				}
			}
		});
	}

}
