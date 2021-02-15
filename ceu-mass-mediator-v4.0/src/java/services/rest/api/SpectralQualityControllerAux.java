package services.rest.api;

import services.rest.api.request.CoelutionType;
import services.rest.api.request.CrossTalkType;
import services.rest.api.request.SpectralQualityControllerQuery;
import services.rest.api.response.SpectralQualityControllerResults;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class SpectralQualityControllerAux {

    private int avgMSIntensity;
    private CoelutionType coElution;
    private double coElutionScore;
    private CrossTalkType crossTalk;
    private double crossTalkScore;
    private double finalScore;
    private double intensityScore;
    private int noisePercentage;
    private double noiseScore;
    private int numSamples;
    private int numScans;
    private int overallMSMSIntensity;
    private double scansScore;

    protected static SpectralQualityControllerResults processSpectralQuality(
            SpectralQualityControllerQuery sqcq) {

        SpectralQualityControllerAux aux = new SpectralQualityControllerAux(sqcq);
        aux.calculateFinalScore();

        return new SpectralQualityControllerResults(
                aux.getFinalScore(),
                aux.getIntensityScore(),
                aux.getNoiseScore(),
                aux.getScansScore(),
                aux.getCoElutionScore(),
                aux.getCrossTalkScore());
    }

    private SpectralQualityControllerAux(SpectralQualityControllerQuery sqcq) {
        this.avgMSIntensity = sqcq.getAverageSignal();
        this.overallMSMSIntensity = sqcq.getIntensity();
        this.noisePercentage = sqcq.getNoise();
        this.numScans = sqcq.getScans();
        this.numSamples = sqcq.getSamples();
        this.coElution = sqcq.getCoelution();
        this.crossTalk = sqcq.getCrosstalk();
    }

    private void calculateFinalScore() {
        if (this.coElution == CoelutionType.WITHUNKNOWNCOMPOUND) {
            this.calculateScoreScans();
            this.calculateScoreNoisePercentage();
            this.calculateScoreOverallMSMSIntensity();
            this.calculateScoreCrossTalk();
            this.coElutionScore = 0;
            this.finalScore = 0;
        }
        else {
            this.calculateScoreScans();
            this.calculateScoreNoisePercentage();
            this.calculateScoreOverallMSMSIntensity();
            this.calculateScoreCrossTalk();
            this.calculateScoreCoElution();
            this.finalScore = this.crossTalkScore + this.coElutionScore + this.scansScore + this.noiseScore + this.intensityScore;
        }
    }

    private void calculateScoreOverallMSMSIntensity() {
        double scoreOverallMSMSIntensity = 0;
        double minRangeScore;
        double maxRangeScore;
        if (this.avgMSIntensity < 10000) {
            minRangeScore = 100;
            maxRangeScore = 1000;
            if (this.overallMSMSIntensity < minRangeScore) {
                scoreOverallMSMSIntensity = 0;
            }
            else if (this.overallMSMSIntensity >= maxRangeScore) {
                scoreOverallMSMSIntensity = 1;
            }
            else {
                scoreOverallMSMSIntensity = (this.overallMSMSIntensity - minRangeScore) / (maxRangeScore - minRangeScore);
            }
        }
        else if (this.avgMSIntensity < 100000) {
            minRangeScore = 1000;
            maxRangeScore = 10000;
            if (this.overallMSMSIntensity < minRangeScore) {
                scoreOverallMSMSIntensity = 0;
            }
            else if (this.overallMSMSIntensity >= maxRangeScore) {
                scoreOverallMSMSIntensity = 1;
            }
            else {
                scoreOverallMSMSIntensity = (this.overallMSMSIntensity - minRangeScore) / (maxRangeScore - minRangeScore);
            }
        }
        else if (this.avgMSIntensity < 1000000) {
            minRangeScore = 1000;
            maxRangeScore = 10000;
            if (this.overallMSMSIntensity < minRangeScore) {
                scoreOverallMSMSIntensity = 0;
            }
            else if (this.overallMSMSIntensity >= maxRangeScore) {
                scoreOverallMSMSIntensity = 1;
            }
            else {
                scoreOverallMSMSIntensity = (this.overallMSMSIntensity - minRangeScore) / (maxRangeScore - minRangeScore);
            }
        }
        else if (this.avgMSIntensity < 10000000) {
            minRangeScore = 10000;
            maxRangeScore = 100000;
            if (this.overallMSMSIntensity < minRangeScore) {
                scoreOverallMSMSIntensity = 0;
            }
            else if (this.overallMSMSIntensity >= maxRangeScore) {
                scoreOverallMSMSIntensity = 1;
            }
            else {
                scoreOverallMSMSIntensity = (this.overallMSMSIntensity - minRangeScore) / (maxRangeScore - minRangeScore);
            }
        }
        else if (this.avgMSIntensity < 100000000) {
            minRangeScore = 100000;
            maxRangeScore = 1000000;
            if (this.overallMSMSIntensity < minRangeScore) {
                scoreOverallMSMSIntensity = 0;
            }
            else if (this.overallMSMSIntensity >= maxRangeScore) {
                scoreOverallMSMSIntensity = 1;
            }
            else {
                scoreOverallMSMSIntensity = (this.overallMSMSIntensity - minRangeScore) / (maxRangeScore - minRangeScore);
            }
        }
        else {
            minRangeScore = 100000;
            maxRangeScore = 1000000;
            if (this.overallMSMSIntensity < minRangeScore) {
                scoreOverallMSMSIntensity = 0;
            }
            else if (this.overallMSMSIntensity >= maxRangeScore) {
                scoreOverallMSMSIntensity = 1;
            }
            else {
                scoreOverallMSMSIntensity = (this.overallMSMSIntensity - minRangeScore) / (maxRangeScore - minRangeScore);
            }
        }

        if (this.noisePercentage <= 5 && scoreOverallMSMSIntensity < 0.3) {
            scoreOverallMSMSIntensity = scoreOverallMSMSIntensity + 0.2;
        }
        else if (this.noisePercentage <= 5 && scoreOverallMSMSIntensity < 0.5) {
            scoreOverallMSMSIntensity = 0.5;
        }
        this.intensityScore = scoreOverallMSMSIntensity;
    }

    private void calculateScoreNoisePercentage() {
        double scoreNoisePercentage = 0;
        double minRangeScore = 5;
        double maxRangeScore = 20;
        if (this.noisePercentage <= minRangeScore) {
            scoreNoisePercentage = 1;
        }
        else if (this.noisePercentage > maxRangeScore) {
            scoreNoisePercentage = 0;
        }
        else {
            scoreNoisePercentage = Math.abs(1 - (this.noisePercentage - minRangeScore) / (maxRangeScore - minRangeScore));
        }
        this.noiseScore = scoreNoisePercentage;
    }

    private void calculateScoreScans() {
        double scoreNumScans = 0;
        int minRangeScore = 3;
        int maxRangeScore = 5;
        if (this.numSamples > 1) {
            scoreNumScans = 1;
        }
        else if (this.numScans < minRangeScore) {
            scoreNumScans = 0;
        }
        else if (this.numScans > maxRangeScore) {
            scoreNumScans = 1;
        }
        else if (this.numScans == 5) {
            scoreNumScans = 0.75;
        }
        else if (this.numScans == 4) {
            scoreNumScans = 0.5;
        }
        else if (this.numScans == 3) {
            scoreNumScans = 0.25;
        }
        this.scansScore = scoreNumScans;
    }

    private void calculateScoreCoElution() {
        this.coElutionScore = this.coElution.value() / 2;
    }

    private void calculateScoreCrossTalk() {
        this.crossTalkScore = this.crossTalk.value() / 2;
    }

    private double getCoElutionScore() {
        return coElutionScore;
    }

    private double getCrossTalkScore() {
        return crossTalkScore;
    }

    private double getFinalScore() {
        return finalScore;
    }

    private double getIntensityScore() {
        return intensityScore;
    }

    private double getNoiseScore() {
        return noiseScore;
    }

    private double getScansScore() {
        return scansScore;
    }

}
