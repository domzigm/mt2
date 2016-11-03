/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */

#include "HelperRoutines.h"

using namespace cv;

namespace mt
{

float slope(Vec4i points)
{
	return (float)(atan2(points[1] - points[3], points[2] - points[0]) * 180.0f / CV_PI);
}

void bwEdgeDetection(const Mat& srcImage, Mat& dstImage, bool useHls, double lowerThres, double upperThresh)
{
	Mat mlow, mhigh, hls, split_hls[3];
	Mat morph3 = getStructuringElement(MORPH_RECT, Size(3, 3));
	Mat morph5 = getStructuringElement(MORPH_RECT, Size(5, 5));

	if (srcImage.channels() == 1) {
		// Source image has only one channel, assume it's gray
		srcImage.copyTo(split_hls[1]);
	}
	else {
		// Source image has more than one channel
		if (useHls) {
			// Convert BGR to HLS color
			cvtColor(srcImage, hls, CV_BGR2HLS);
			split(hls, split_hls);
		}
		else {
			// Convert BGR to GRAY
			cvtColor(srcImage, split_hls[1], CV_BGR2GRAY);
		}
	}

	GaussianBlur(split_hls[1], split_hls[1], Size(5, 5), 0);
	equalizeHist(split_hls[1], hls);

	threshold(hls, mlow, lowerThres, 255, CV_THRESH_BINARY_INV);
	threshold(hls, mhigh, upperThresh, 255, CV_THRESH_BINARY);

	morphologyEx(mlow, mlow, MORPH_DILATE, morph5);
	morphologyEx(mhigh, mhigh, MORPH_DILATE, morph5);
	dstImage = mlow & mhigh;
	morphologyEx(dstImage, dstImage, MORPH_CLOSE, morph3);
}

void inHsvRange(const Mat& srcImage, const hsvPlanes& desc, cv::Mat& dstImage)
{
	cv::Mat left;
	cv::Mat right;

	inRange(srcImage, desc.loThreshold_Left, desc.hiThreshold_Left, left);

	// If right low == right high threshold, ignore second part
	if (desc.loThreshold_Right[0] != desc.hiThreshold_Right[0]) {

		inRange(srcImage, desc.loThreshold_Right, desc.hiThreshold_Right, right);
		dstImage = left | right;
	}
	else {
		
		dstImage = left;
	}
}

void inHsvRange(const Mat& srcImage, const colorDescription& desc, cv::Mat& dstImage)
{
	inHsvRange(srcImage, desc.lowerThreshold, desc.upperThreshold, dstImage);
}

void inHsvRange(const cv::Mat& srcImage, cv::Scalar lowThres, cv::Scalar highThres, cv::Mat& dstImage)
{
	// If low threshold is bigger than high threshold, this means wrap around for red
	if (lowThres[0] > highThres[0]) {

		cv::Mat first;
		cv::Mat second;

		auto temp = highThres[0];
		highThres[0] = 179.f;
		inRange(srcImage, lowThres, highThres, first);

		highThres[0] = temp;
		lowThres[0] = 0.f;
		inRange(srcImage, lowThres, highThres, second);

		dstImage = first | second;
	}
	else {
		inRange(srcImage, lowThres, highThres, dstImage);
	}
}

//! Convert absolute (x,y) rect values into relative values
void rectToRelative(const Mat& mapping, const Rect& input_rect, Rect2f& output_rect)
{
	output_rect.x = (float)(input_rect.x / mapping.cols);
	output_rect.y = (float)(input_rect.y / mapping.rows);
	output_rect.width = (float)(input_rect.width / mapping.cols);
	output_rect.height = (float)(input_rect.height / mapping.rows);
}

//! Convert relative rect values into absolute (x,y) values
void rectToAbsolute(const Mat& mapping, const Rect2f& input_rect, Rect& output_rect)
{
	output_rect.x = (int)(input_rect.x * mapping.cols);
	output_rect.y = (int)(input_rect.y * mapping.rows);
	output_rect.width = (int)(input_rect.width * mapping.cols);
	output_rect.height = (int)(input_rect.height * mapping.rows);
}

//! This will also include a bit of the surrounding area
void morphOps(Mat& binaryImage)
{
	Mat erodeElement = getStructuringElement(MORPH_RECT, Size(3, 3));
	Mat dilateElement = getStructuringElement(MORPH_RECT, Size(7, 7));

	erode(binaryImage, binaryImage, erodeElement, Point(-1, -1), 2);
	//erode(binaryImage, binaryImage, erodeElement);

	dilate(binaryImage, binaryImage, dilateElement, Point(-1, -1), 2);
	//dilate(binaryImage, binaryImage, dilateElement);
}

//! Draw bounding boxes on an image based on a binary mask
uint32_t drawBoundingBoxes(Mat& destination, Mat& mask, Scalar color)
{
	std::vector<std::vector<Point>> contours;
	std::vector<Vec4i> hierarchy;
	uint32_t i = 0;
	Rect bounding_rect;

	findContours(mask, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE);
	for (; i < contours.size(); i++) {
		
		bounding_rect = boundingRect(contours[i]);
		rectangle(destination, bounding_rect, color, 2, 8, 0);
	}

	return i;
}

}
