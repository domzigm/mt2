/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */

#include "FigureDetection.h"
#include "HelperRoutines.h"

using namespace cv;

namespace mt
{

uint8_t locateFigure(const colorDescription& desc, const Mat& srcimage, Rect& rect)
{
	// Assume the srcimage is already in HSV color space
	Mat dstimage;
	std::vector<std::vector<Point>> contours;
	std::vector<Vec4i> hierarchy;
	uint32_t largest_contour_index = 0;
	uint32_t largest_area = 0;

	inHsvRange(srcimage, desc, dstimage);
	morphOps(dstimage);

	findContours(dstimage, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE);

	for (uint32_t i = 0; i < contours.size(); i++) {

		//  Find the area of contour
		uint32_t a = (uint32_t)contourArea(contours[i], false);
		if (a > largest_area) {

			// Store the index of largest contour
			largest_contour_index = i;
			largest_area = a;
		}
	}

	if (contours.size() > 0) {

		rect = boundingRect(contours[largest_contour_index]);
		return 1;
	}

	return 0;
}

}
