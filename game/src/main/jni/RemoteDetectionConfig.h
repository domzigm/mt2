/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */

#pragma once

#include <opencv2/opencv.hpp>

#include <stdint.h>
#include <vector>

namespace mt
{

struct RemoteDetectionConfig
{
	//! Normalized downsample width ratio
	double scaleFactX;

	//! Normalized downsample height ratio
	double scaleFactY;

	//! Normalized distance from the marker to the button
	std::vector<cv::Vec2f> buttonDistances;
};

}
