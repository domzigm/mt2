/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */
 
#pragma once

#include <stdint.h>

namespace mt
{

struct BoardRectificationConfig
{
	/**
	Note: There are four ROIs, one in each corner. The input size is based the percentage specified by sourceRoi[Width/Height]
	**/

	//! Width of the rectified image in pixels
	int outputImageWidth;
	
	//! Height of the rectified image in pixels
	int outputImageHeight;

	//! The source roi width in percent
	float sourceRoiWidth;

	//! The source roi height in percent
	float sourceRoiHeight;

	//! Scale factor for the analyze roi size
	float analyzeRoiScale;

	//! Boardedge detection canny
	double cannyTh;

	//! Boardedge detection houghline threshold
	int houghTh;

	//! Boardedge detection houhline minimum length
	double houghMinLen;

	//! Boardedge detection houghline maximum gap
	double houghMaxGap;
};

}
