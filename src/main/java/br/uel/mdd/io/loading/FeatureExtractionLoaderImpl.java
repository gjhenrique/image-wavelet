package br.uel.mdd.io.loading;

import br.uel.mdd.avaliation.Index;
import br.uel.mdd.dao.ExtractionsDao;
import br.uel.mdd.db.tables.pojos.Extractions;
import br.uel.mdd.db.tables.pojos.Extractors;
import br.uel.mdd.db.tables.pojos.Images;
import br.uel.mdd.extractor.FeatureExtractor;
import br.uel.mdd.extractor.FeatureNotImplemented;
import br.uel.mdd.io.ImageWrapper;
import br.uel.mdd.utils.ExtractorUtils;
import br.uel.mdd.utils.PrimitiveUtils;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

public class FeatureExtractionLoaderImpl implements FeatureExtractionLoader {

    private FeatureExtractor featureExtractor;

    private Extractors extractor;

    private ExtractionsDao extractionsDao;

    private Index index;

    private final Logger logger = LoggerFactory.getLogger(FeatureExtractionLoaderImpl.class);

    @Inject
    public FeatureExtractionLoaderImpl(@Assisted Extractors extractor,
                                       ExtractionsDao extractionsDao,
                                       Index index) {
        this.featureExtractor = ExtractorUtils.getFeatureExtractorImplementation(extractor);
        this.extractor = extractor;
        this.extractionsDao = extractionsDao;
        this.index = index;
    }

    public void extractFeatures(List<Images> images) {
        for (Images image : images) {
            extractFeatures(image);
        }
    }

    public void extractFeatures(Images image) {
        logger.info("Extracting feature of image {} with extractor {}", image.getFileName(), featureExtractor);

        Extractions extractions = extractionsDao.fetchImageIdAndExtractorId(image.getId(), extractor.getId());

        if (extractions == null) {

            ImageWrapper wrapper = this.getImageWrapper(image);

            long start = System.nanoTime();
            double[] features;
            try {
                features = featureExtractor.extractFeature(wrapper);
            } catch (FeatureNotImplemented ex) {
                logger.info(ex.getMessage());
                return;
            }
            long elapsedTime = System.nanoTime() - start;

            Double[] featuresContainer = PrimitiveUtils.castPrimitiveToWrapper(features);

            extractions = this.buildExtraction(featuresContainer, image, elapsedTime);
            extractionsDao.insertNullPk(extractions);

            index.addEntry(extractions);

            logger.debug("Inserted extraction {} in the database", extractions.getId());
        } else {
            logger.info("Extraction with Image {} and Extractor {} already exists in the database", image.getId(), extractor.getId());
        }
    }

    private ImageWrapper getImageWrapper(Images image) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image.getImage());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(byteArrayInputStream);

        return ImageWrapper.createImageOpener(bufferedInputStream, image.getMimeType());
    }


    private Extractions buildExtraction(Double[] features, Images image, long elapsedTime) {
        Extractions extractions = new Extractions();

        extractions.setExtractionData(features);
        extractions.setExtractorId(extractor.getId());
        extractions.setImageId(image.getId());
        extractions.setExtractionTime(elapsedTime);

        return extractions;
    }
}
