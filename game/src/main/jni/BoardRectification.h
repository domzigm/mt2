/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */

#pragma once

#include "BoardRectificationConfig.h"

#include <opencv2/opencv.hpp>

namespace mt
{

class BoardRectification
{

public:
	BoardRectification(BoardRectificationConfig& config) :
		m_config(config),
		m_rectifyValid(false)
	{};

	bool updateBoard(const cv::Mat& cameraImage);
	void rectifyImage(const cv::Mat& cameraImage, cv::Mat& rectifiedImage);

	enum marker
	{
		TOP_LEFT,
		TOP_RIGHT,
		BOT_RIGHT,
		BOT_LEFT,
		MARKER_MAX
	};

private:

	BoardRectificationConfig&	m_config;
	bool						m_rectifyValid;
	cv::Mat						m_boardEdges[MARKER_MAX];
	cv::Point2f					m_rectifyCoords[MARKER_MAX];
};

}

