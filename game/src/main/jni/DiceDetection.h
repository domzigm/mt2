/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */
 
#pragma once

#include "DiceDetectionConfig.h"

#include <opencv2\opencv.hpp>
#include <vector>

namespace mt
{

struct DiceCandidate
{
	int id;
	int area;
	cv::Point2i pt;
};

class DiceDetection
{
public:
	DiceDetection(DiceDetectionConfig& config) :
		m_config(config) {};

	uint8_t processImage(const cv::Mat& sourceImage);
	uint8_t getResult(uint32_t& dices, uint32_t& eyes);
	uint8_t drawBoxes(cv::Mat& image);

private:
	DiceDetectionConfig&	m_config;
	uint32_t				m_eyeCounter;
	std::vector< std::vector<DiceCandidate> > m_dices;
};

}
