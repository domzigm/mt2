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

struct DiceDetectionConfig
{
	/**
	Note: When changing scaleFact[X/Y], you have to manually adjust fillTh[Low/High] and maxEyeDistance!
	**/
  
	//! Undersampling the input image when searching circles in lines
	int subSample;
  
	//! Low threshold of pixels required to fill a circle
	int fillThLow;
  
	//! High threshold of pixels required to fill a circle
	int fillThHigh;
  
	//! Canny threshold1
	double cannyTh1;
  
	//! Canny threshold2
	double cannyTh2;
  
	//! Normalized downsample width before canny operation 
	double scaleFactX;
  
	//! Normalized downsample height before canny operation
	double scaleFactY;
  
	//! Maximum distance between eyes on a single dice
	double maxEyeDistance;
};

}
