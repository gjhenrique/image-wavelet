/**
 Format:
 -- Author: <name>
 -- Date/Time: <date>
 */

-- Pedro
-- 30/05/14
ALTER TABLE dataset_classes
    ADD UNIQUE (class_id, dataset_id);
ALTER TABLE images
    DROP CONSTRAINT fk_images_datasets,
    DROP COLUMN dataset_id,
    ADD COLUMN dataset_class_id INT,
    ADD CONSTRAINT fk_images_dataset_classes FOREIGN KEY (dataset_class_id)
      REFERENCES dataset_classes(id) ON UPDATE CASCADE ON DELETE RESTRICT;

-- Author: Guilherme
-- Date/Time: 30/05/2014 15:59
ALTER TABLE extractors DROP COLUMN family;
ALTER TABLE extractors DROP COLUMN support;
ALTER TABLE extractors ADD COLUMN filter_identifier VARCHAR ( 50 );
ALTER TABLE extractors ADD COLUMN class_name VARCHAR ( 50 );
ALTER TABLE extractors ADD COLUMN levels_wavelet INT;

-- Author: Guilherme
-- Date/Time: 30/05/2014 15:59
INSERT INTO extractors (id, name, filter_identifier, class_name, levels_wavelet)
VALUES (1, 'Wavelet SubEspaco', 'Haar1', 'ReducedScaleWaveletExtractor', 4);

INSERT INTO extractors (id, name, filter_identifier, class_name, levels_wavelet)
VALUES (2, 'Wavelet SubEspaco', 'Daubechies2', 'ReducedScaleWaveletExtractor', 4);

INSERT INTO extractors (id, name, filter_identifier, class_name, levels_wavelet)
VALUES (3, 'Wavelet SubEspaco', 'Symlets2', 'ReducedScaleWaveletExtractor', 4);

-- Author: Guilherme
-- Date/Time: 31/05/2014 12:55
ALTER TABLE extractions DROP COLUMN extraction_data;
ALTER TABLE extractions ADD COLUMN extraction_data DOUBLE PRECISION [];

-- Author: Guilherme
-- Date/Time: 31/05/2014 16:38
ALTER TABLE extractions ADD UNIQUE (image_id, extractor_id);
ALTER TABLE extractions ALTER COLUMN image_id SET NOT NULL;
ALTER TABLE extractions ALTER COLUMN extractor_id SET NOT NULL;

-- Author: Guilherme
-- Date/Time: 31/05/2014 19:31
ALTER TABLE class_image ADD UNIQUE (name);
ALTER TABLE datasets ADD UNIQUE (name);
ALTER TABLE images ADD UNIQUE (file_name, dataset_class_id);