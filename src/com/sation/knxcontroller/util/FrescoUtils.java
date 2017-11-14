package com.sation.knxcontroller.util;

public class FrescoUtils {

//	public static Bitmap getBitmapThroughFresco(String filePath) {
//		ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
//			    .setBackgroundColor(Color.GREEN)
//			    .build();
//		Uri fileUri = Uri.parse("file://"+filePath);
//		ImageRequest request = ImageRequestBuilder
//			    .newBuilderWithSource(fileUri)
//			    .setImageDecodeOptions(decodeOptions)
//			    .setAutoRotateEnabled(true)
//			    .setLocalThumbnailPreviewsEnabled(true)
//			    .setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH)
//			    .setProgressiveRenderingEnabled(false)
//			    .build();
//		ImagePipeline imagePipeline = Fresco.getImagePipeline();
//		DataSource<CloseableReference<CloseableImage>>
//		    dataSource = imagePipeline.fetchDecodedImage(request, callerContext);
//		dataSource.subscribe(new BaseBitmapDataSubscriber() {
//
//            @Override
//            public void onNewResultImpl(@Nullable Bitmap bitmap) {
//            // You can use the bitmap in only limited ways
//            // No need to do any cleanup.
//
//            }
//
//            @Override
//            public void onFailureImpl(DataSource dataSource) {
//            // No cleanup required here.
//                   }
//          },
//           CallerThreadExecutor.getInstance());
//	}
}
