/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */

#include "BoardRectification.h"
#include "HelperRoutines.h"

using namespace cv;

#define DEBUG 1

namespace mt
{

void BoardRectification::rectifyImage(const Mat& cameraImage, Mat& rectifiedImage)
{
	Point2f dstPts[4];

	// Image will be rectified to a specific static size
	dstPts[0] = Point2f(0, 0);
	dstPts[1] = Point2f((float)m_config.outputImageWidth, 0);
	dstPts[2] = Point2f((float)m_config.outputImageWidth, (float)m_config.outputImageHeight);
	dstPts[3] = Point2f(0, (float)m_config.outputImageHeight);

	Mat copy;
	cameraImage.copyTo(copy);
	cv::circle(copy, m_rectifyCoords[0], 5, Scalar(255, 0, 0), 3);
	cv::circle(copy, m_rectifyCoords[1], 5, Scalar(255, 0, 0), 3);
	cv::circle(copy, m_rectifyCoords[2], 5, Scalar(255, 0, 0), 3);
	cv::circle(copy, m_rectifyCoords[3], 5, Scalar(255, 0, 0), 3);

//	m_rectifyCoords[2].y += m_rectifyCoords[1].y;
//	m_rectifyCoords[3].y += m_rectifyCoords[0].y;

	Mat warp_mat = getPerspectiveTransform(m_rectifyCoords, dstPts);
	warpPerspective(cameraImage, rectifiedImage, warp_mat, rectifiedImage.size());
	rectifiedImage = rectifiedImage(Rect(0, 0, m_config.outputImageWidth, m_config.outputImageHeight));
	//imshow("org", copy);
	//imshow("rect", rectifiedImage);
	waitKey(1);
	
}

bool BoardRectification::updateBoard(const cv::Mat& cameraImage)
{
	int regionsFound = 0;
	cv::Mat grayImage;
	cv::Mat canny;
	cv::cvtColor(cameraImage, grayImage, CV_RGB2GRAY);
	cv::Point2f rectifyCoords[MARKER_MAX];

	//cv::GaussianBlur(grayImage, grayImage, Size(3, 3), 0, 0);
	
	const cv::Point2f coordEdges[MARKER_MAX] = {
		
		cv::Point2f(0.f,							0.f),
		cv::Point2f(1.f - m_config.sourceRoiWidth,	0.f),
		cv::Point2f(1.f - m_config.sourceRoiWidth,	1.f - m_config.sourceRoiHeight),
		cv::Point2f(0.f,							1.f - m_config.sourceRoiHeight)
	};

	for (int region = TOP_LEFT; region < MARKER_MAX; region++) {

		auto rect = Rect(
			(int)(coordEdges[region].x * grayImage.cols),		// X position
			(int)(coordEdges[region].y * grayImage.rows),		// Y position
			(int)(grayImage.cols * m_config.sourceRoiWidth),	// width
			(int)(grayImage.rows * m_config.sourceRoiHeight)	// height
		);

		// Set ROI on gray image
		Mat temp1 = grayImage(rect);
		Mat temp2;
		
		float scale_x = m_config.analyzeRoiScale;
		float scale_y = m_config.analyzeRoiScale;
		
#if 1
		// Might be slower but more stable
		/*
		Mat mlow, mhigh;
		Mat hlss, hls[3];
		//cvtColor(cameraImage(rect), hlss, CV_BGR2HLS);
		//split(hlss, hls);
		temp1.copyTo(hls[1]);
		GaussianBlur(hls[1], hls[1], Size(5, 5), 0);
		equalizeHist(hls[1], temp2);
		//cv::threshold(temp2, canny, 251, 255, CV_THRESH_BINARY);
		//morphologyEx(canny, canny, MORPH_CLOSE, getStructuringElement(MORPH_RECT, Size(3, 3)));

		cv::threshold(temp2, mlow, 3, 255, CV_THRESH_BINARY_INV);
		cv::threshold(temp2, mhigh, 251, 255, CV_THRESH_BINARY);
		morphologyEx(mlow, mlow, MORPH_DILATE, getStructuringElement(MORPH_RECT, Size(5, 5)));
		morphologyEx(mhigh, mhigh, MORPH_DILATE, getStructuringElement(MORPH_RECT, Size(5, 5)));
		canny = mlow & mhigh;
		morphologyEx(canny, canny, MORPH_CLOSE, getStructuringElement(MORPH_RECT, Size(3, 3)));
		*/
		bwEdgeDetection(cameraImage(rect), canny);
		
#else
		// Might be faster but more false positives
		temp1.copyTo(temp2);
		cv::Canny(temp1, canny, m_config.cannyTh, m_config.cannyTh, 3, true);
#endif

		int lineH = 0, lineV = 0;
		std::vector<Vec4i> lines;
		HoughLinesP(canny, lines, 1, CV_PI / 180, m_config.houghTh, m_config.houghMinLen, m_config.houghMaxGap);
		for (size_t i = 0; i < lines.size(); i++) {
			float s = abs(slope(lines[i]));
			if (s >= 0.f && s <= 10.f) {
				lineH++;
#if DEBUG
				line(temp2, Point(lines[i][0], lines[i][1]),
					Point(lines[i][2], lines[i][3]), Scalar(0, 0, 255), 3, 8);
#endif
			}
			if (s >= 80.f && s <= 100.f) {
				lineV++;
#if DEBUG
				line(temp2, Point(lines[i][0], lines[i][1]),
					Point(lines[i][2], lines[i][3]), Scalar(0, 0, 255), 3, 8);
#endif
			}

			
		}

//		imshow("test", temp2);
//		imshow("test1", canny);
//		waitKey(0);
		if (lineH == 0 || lineV == 0) {
			continue;
		}

		std::vector<Point2i> corners;
		goodFeaturesToTrack(temp1, corners, 3, 0.8f, 5, noArray(), 7, true);

		// Calculate smallest distance from image edge to board corner
		double minDistance = max(rect.width, rect.height);
		for (auto corner : corners) {

			double distance = 0.f;
			switch (region)	{

			case TOP_LEFT:
				distance = abs(norm(corner - Point(0, 0)));
				break;
			case TOP_RIGHT:
				distance = abs(norm(corner - Point(rect.width, 0)));
				break;
			case BOT_RIGHT:
				distance = abs(norm(corner - Point(rect.width, rect.height)));
				break;
			case BOT_LEFT:
				distance = abs(norm(corner - Point(0, rect.height)));
				break;
			}

			// We can find up to 3 points for every ROI, find the one with the shortest distance to the edge
			if (distance < minDistance) {

				minDistance = distance;

				rectifyCoords[region].x = (corner.x * scale_x) + rect.x;
				rectifyCoords[region].y = (corner.y * scale_y) + rect.y;
			}
		}

		if (minDistance < max(rect.width, rect.height)) {

			regionsFound++;
		}
	}

	if (MARKER_MAX == regionsFound) {

		m_rectifyValid = 1;
		memcpy(m_rectifyCoords, rectifyCoords, sizeof(rectifyCoords));
	}

	return m_rectifyValid;
}

}
