package com.team.buddyya.feed.service;

import com.team.buddyya.common.domain.S3DirectoryName;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedImage;
import com.team.buddyya.feed.respository.FeedImageRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedImageService {
    private final S3UploadService s3UploadService;
    private final FeedImageRepository feedImageRepository;

    public List<FeedImage> uploadFeedImages(Feed feed, List<MultipartFile> images) {
        return images.stream()
                .map(image -> uploadFeedImage(feed, image))
                .toList();
    }

    private FeedImage uploadFeedImage(Feed feed, MultipartFile image) {
        String imageUrl = s3UploadService.uploadFile(S3DirectoryName.FEED_IMAGE.getDirectoryName(), image);
        FeedImage feedImage = FeedImage.builder()
                .feed(feed)
                .url(imageUrl)
                .build();
        return feedImageRepository.save(feedImage);
    }
}
