package com.jaydi.ruby.apis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;

public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ImageServlet.class.getName());

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private ImagesService imagesService = ImagesServiceFactory.getImagesService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// allow cross domain usage
		res.setHeader("Access-Control-Allow-Origin", "*");
		
		Map<String, List<BlobKey>> blobKeyMap = blobstoreService.getUploads(req);
		List<BlobKey> blobKeys = blobKeyMap.get("image");

		if (blobKeys == null || blobKeys.isEmpty()) {
			log.warning("no blob uploaded");
			return;
		}

		PrintWriter out = res.getWriter();
		out.print(blobKeys.get(0).getKeyString());
		out.flush();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		// allow cross domain usage
		res.setHeader("Access-Control-Allow-Origin", "*");
		
		// get blob key parameter
		String blobKey = req.getParameter("blob-key");

		// return upload url if blob key null or empty
		if (blobKey == null || blobKey.isEmpty()) {
			PrintWriter out = res.getWriter();
			out.print(blobstoreService.createUploadUrl("/image"));
			out.flush();
			return;
		}

		// get image with blob key
		Image image = ImagesServiceFactory.makeImageFromBlob(new BlobKey(blobKey));
		// dummy transform to get image parameters
		Transform dummy = ImagesServiceFactory.makeRotate(360);
		image = imagesService.applyTransform(dummy, image);

		// get width and height request parameters, 0 if none
		int width = 0;
		int height = 0;
		try {
			width = Integer.valueOf(req.getParameter("width"));
		} catch (NumberFormatException e) {
			width = 0;
		}
		try {
			height = Integer.valueOf(req.getParameter("height"));
		} catch (NumberFormatException e) {
			height = 0;
		}

		// apply transform based on requested width and height
		if (width > 0 && height > 0) {
			Transform resize = makeOutsideResize(image, width, height);
			image = imagesService.applyTransform(resize, image);

			Transform centerCrop = makeCenterCrop(image, width, height);
			OutputSettings settings = new OutputSettings(ImagesService.OutputEncoding.JPEG);
			image = imagesService.applyTransform(centerCrop, image, settings);
		} else if (width > 0 || height > 0) {
			Transform resize = makeInsideResize(image, width, height);
			OutputSettings settings = new OutputSettings(ImagesService.OutputEncoding.JPEG);
			image = imagesService.applyTransform(resize, image, settings);
		}

		// response
		res.addHeader("Content-Type", "image/" + image.getFormat().toString());
		res.getOutputStream().write(image.getImageData());
	}

	private Transform makeOutsideResize(Image image, float targetWidth, float targetHeight) {
		float width = image.getWidth();
		float height = image.getHeight();
		float wratio = width / targetWidth;
		float hratio = height / targetHeight;
		int size;

		if (wratio > hratio)
			size = (int) (width / hratio);
		else
			size = (int) (height / wratio);

		return ImagesServiceFactory.makeResize(size, size);
	}

	private Transform makeInsideResize(Image image, float targetWidth, float targetHeight) {
		float width = image.getWidth();
		float height = image.getHeight();
		int wsize;
		int hsize;

		if (targetWidth > 0 && targetHeight > 0) {
			wsize = (int) targetWidth;
			hsize = (int) targetHeight;
		} else if (targetWidth > 0) {
			wsize = (int) targetWidth;
			hsize = (int) (height * (targetWidth / width));
		} else if (targetHeight > 0) {
			wsize = (int) (width * (targetHeight / height));
			hsize = (int) targetHeight;
		} else {
			wsize = (int) width;
			hsize = (int) height;
		}

		return ImagesServiceFactory.makeResize(wsize, hsize);
	}

	private Transform makeCenterCrop(Image image, float targetWidth, float targetHeight) {
		float width = image.getWidth();
		float height = image.getHeight();
		float wratio = width / targetWidth;
		float hratio = height / targetHeight;

		float leftX = 0;
		float topY = 0;
		float rightX = 1;
		float bottomY = 1;

		if (wratio > hratio) {
			float move = ((width - height * (targetWidth / targetHeight)) / 2) / width;
			leftX += move;
			rightX -= move;
		} else {
			float move = ((height - width * (targetHeight / targetWidth)) / 2) / height;
			topY += move;
			bottomY -= move;
		}

		return ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY);
	}
}
