package br.uel.mdd.extractor;

import br.uel.mdd.io.ImageWrapper;
import de.lmu.ifi.dbs.jfeaturelib.features.*;
import ij.ImagePlus;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.List;

public class JFeatureLib implements FeatureExtractor {

    private AbstractFeatureDescriptor abstractFeatureDescriptor;

    @Inject
    public JFeatureLib(AbstractFeatureDescriptor abstractFeatureDescriptor) {
        this.abstractFeatureDescriptor = abstractFeatureDescriptor;
    }

    @Override
    public double[] extractFeature(ImageWrapper imageWrapper) {
        BufferedImage bufferedImage = imageWrapper.getImage();
        ImagePlus imagePlus = new ImagePlus("TITLE", bufferedImage);

        // Avoid getting the same results for different images
        abstractFeatureDescriptor.getFeatures().clear();
        abstractFeatureDescriptor.run(imagePlus.getProcessor());

        List<double[]> featuresList = abstractFeatureDescriptor.getFeatures() ;

        double[] features = featuresList.get(0);

//        Checking if it is Nan due to any unhandled division by 0
        for (int i = 0; i < features.length; i++) {
           if(Double.isNaN(features[i])) {
               features[i] = 0;
           }
        }
        return features;
    }

    @Override
    public String toString() {
        String descriptorName = null;

        if(abstractFeatureDescriptor instanceof Haralick)
            descriptorName = "Haralick";
        else if(abstractFeatureDescriptor instanceof Tamura)
            descriptorName = "Tamura";
        else if(abstractFeatureDescriptor instanceof CEDD)
            descriptorName = "CEDD";
        else if(abstractFeatureDescriptor instanceof Thumbnail)
            descriptorName = "Thumbnail";
        else if(abstractFeatureDescriptor instanceof FCTH)
            descriptorName = "FCTH";
        else if(abstractFeatureDescriptor instanceof JCD)
            descriptorName = "JCD";
        else if(abstractFeatureDescriptor instanceof LuminanceLayout)
            descriptorName = "Luminance Layout";

        return descriptorName;
    }

}
