package com.leucine.streem.service.impl;

import com.leucine.streem.config.MediaConfig;
import com.leucine.streem.constant.ErrorMessage;
import com.leucine.streem.constant.Misc;
import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.mapper.IMediaMapper;
import com.leucine.streem.dto.request.MediaRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.dto.response.MediaUploadResponse;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.FileStorageException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.model.Media;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.IMediaRepository;
import com.leucine.streem.repository.IOrganisationRepository;
import com.leucine.streem.repository.IUserRepository;
import com.leucine.streem.service.IMediaService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MediaService implements IMediaService {
  private final IMediaRepository mediaRepository;
  private final MediaConfig mediaConfig;
  private final IUserRepository userRepository;
  private final IMediaMapper mediaMapper;
  private final IOrganisationRepository organisationRepository;

  //TODO authorise user to access media of its organisation only.
  @Autowired
  public MediaService(MediaConfig mediaConfig, IUserRepository userRepository, IMediaRepository mediaRepository,
                      IMediaMapper mediaMapper, IOrganisationRepository organisationRepository) throws FileStorageException {
    this.mediaConfig = mediaConfig;
    this.userRepository = userRepository;
    this.mediaRepository = mediaRepository;
    this.mediaMapper = mediaMapper;
    this.organisationRepository = organisationRepository;
    try {
      Files.createDirectories(Paths.get(mediaConfig.getLocation()).toAbsolutePath().normalize());
    } catch (Exception ex) {
      throw new FileStorageException(ErrorMessage.COULD_NOT_CREATE_THE_DIRECTORY_WHERE_THE_UPLOADED_FILES_WILL_BE_STORED, ex);
    }
  }

  @Override
  public MediaUploadResponse save(MediaRequest mediaRequest, MultipartFile file) {
    log.info("[save] Request to save file, mediaRequest: {}, originalFileName: {}, size: {}", mediaRequest, file.getOriginalFilename(), file.getSize());
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    validateFileExtension(new MultipartFile[]{file});
    var media = createMedia(mediaRequest, file);
    media.setOrganisation(organisationRepository.getOne(principalUser.getOrganisationId()));
    media = mediaRepository.save(media);
    return mediaMapper.toMediaUploadResponsse(media);
  }

  @Override
  public MediaUploadResponse save(MultipartFile file) {
    log.info("[save] Request to save file, originalFileName: {}, size: {}", file.getOriginalFilename(), file.getSize());
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    validateFileExtension(new MultipartFile[]{file});
    var media = createMedia(file);
    media.setOrganisation(organisationRepository.getOne(principalUser.getOrganisationId()));
    media = mediaRepository.save(media);
    return mediaMapper.toMediaUploadResponsse(media);
  }

  @Override
  public List<MediaUploadResponse> save(MultipartFile[] files) {
    log.info("[save] Request to save multiple file");
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    validateFileExtension(files);
    List<Media> medias = new ArrayList<>();
    for (MultipartFile file : files) {
      var media = createMedia(file);
      media.setOrganisation(organisationRepository.getOne(principalUser.getOrganisationId()));
      medias.add(media);
    }
    mediaRepository.saveAll(medias);
    return mediaMapper.toMediaUploadResponsse(medias);
  }

  @Override
  public MediaDto update(Long mediaId, MediaRequest mediaRequest) throws ResourceNotFoundException {
    log.info("[update] Request to update file, mediaId: {}", mediaId);
    var media = mediaRepository.findById(mediaId)
            .orElseThrow(() -> new ResourceNotFoundException(mediaId, ErrorCode.MEDIA_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    if(!Utility.isEmpty(mediaRequest.getName())) {
      media.setName(mediaRequest.getName());
    }

    if(!Utility.isEmpty(mediaRequest.getDescription())) {
      media.setDescription(mediaRequest.getDescription());
    }

    return mediaMapper.toDto(mediaRepository.save(media));
  }

  @Override
  public MediaDto archive(Long mediaId) throws ResourceNotFoundException {
    log.info("[archive] Request to archive file, mediaId: {}", mediaId);
    var media = mediaRepository.findById(mediaId)
      .orElseThrow(() -> new ResourceNotFoundException(mediaId, ErrorCode.MEDIA_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    media.setArchived(true);
    return mediaMapper.toDto(media);
  }

  private Media createMedia(MultipartFile file) {
    return createMedia(new MediaRequest(), file);
  }

  public Media createMedia(MediaRequest mediaRequest, MultipartFile multipartFile) {
    var principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var user = userRepository.getOne(principalUser.getId());
    String fileName = Utility.normalizeFilePath(multipartFile.getOriginalFilename());
    try {
      // Check if the file name contains invalid characters
      if (fileName.contains("..")) {
        throw new FileStorageException(MessageFormat.format(ErrorMessage.FILENAME_CONTAINS_INVALID_CHARACTERS, fileName));
      }

      String originalFilename = multipartFile.getOriginalFilename();
      assert originalFilename != null;
      String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
      String relativePath = principalUser.getOrganisationId() + File.separator + DateTimeUtils.getYear() + File.separator + DateTimeUtils.getNumericMonth();
      String absolutePath = mediaConfig.getLocation() + File.separator + relativePath;
      fileName = Utility.generateUnique() + "." + extension;
      var path = Paths.get(absolutePath).toAbsolutePath().normalize();
      if (!Files.exists(path)) {
        Files.createDirectories(path);
      }
      var targetLocation = path.resolve(fileName);
      Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      var media = new Media();
      media.setName(mediaRequest.getName())
        .setDescription(mediaRequest.getDescription())
        .setFilename(fileName)
        .setOriginalFilename(originalFilename)
        .setType(multipartFile.getContentType())
        .setRelativePath(Misc.RELATIVE_FILE_PATH + File.separator + relativePath)
        .setCreatedBy(user)
        .setModifiedBy(user)
        .setId(mediaRequest.getMediaId());
      return media;
    } catch (IOException ex) {
      throw new FileStorageException(MessageFormat.format(ErrorMessage.FILE_STORAGE, fileName), ex);
    }
  }

  private void validateFileExtension(MultipartFile[] multipartFiles) {
    String[] fileExtension = mediaConfig.getFileTypes().split(",");
    Set<String> fileExtensionSet = Arrays.stream(fileExtension).collect(Collectors.toSet());
    for (MultipartFile multipartFile : multipartFiles) {
      String fileName = multipartFile.getOriginalFilename();
      if (!Utility.isEmpty(fileName)) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!fileExtensionSet.contains(extension)) {
          throw new FileStorageException(ErrorMessage.INVALID_EXTENSION);
        }
      }

    }
  }
}
