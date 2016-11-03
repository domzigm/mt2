/*
*
* (c) domzigm 2016 - GPLv3
* https://github.com/domzigm/mt
*
*/

#pragma once

#include <stdint.h>
#include <opencv2/opencv.hpp>

namespace mt
{

//! See https://en.wikipedia.org/wiki/HSL_and_HSV
struct hsvPlanes
{
	cv::Scalar loThreshold_Left;
	cv::Scalar hiThreshold_Left;
	cv::Scalar loThreshold_Right;
	cv::Scalar hiThreshold_Right;
};

struct colorDescription
{
	cv::Scalar lowerThreshold;
	cv::Scalar upperThreshold;
};

}