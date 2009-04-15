/*
 * libqcw - A library for parsing, manipulating, and writing QCharts (TM) 
 * workspace files.
 *
 * Copyright (C) 2008 Jon Nall
 * QCharts is a registered service mark of eSignal, Inc.
 * RagingBull is a registered service mark of eSignal, Inc.
 *
 * Licensed under the Open Software License version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You should
 * have received a copy of the License along with this software; if not, you
 * may obtain a copy of the License at
 *
 * http://opensource.org/licenses/osl-2.0.php
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.stuntaz.libqcw.defines;

import org.stuntaz.libqcw.QCWException;

/**
 * An enumeration of study types supported by QCW files.
 * 
 * @author nall
 *
 */
public enum QStudyType
{

    // Upper Studies
    /**
     * A moving average study
     */
    MovingAverage(0x0001),

    /**
     * Bollinger Band study
     */
    BollingerBands(0x0002),

    /**
     * Envelope study
     */
    Envelope(0x0003),

    /**
     * Donchian Channel study
     */
    DonchianChannel(0x0004),

    /**
     * An overlay expression
     */
    OverlayExpression(0x0005),

    /**
     * Keltner channel
     */
    KeltnerChannel(0x0008),

    /**
     * Float Turnover Channel
     */
    FloatTurnoverChannel(0x0009),

    /**
     * Volume At Price
     */
    VolumeAtPrice(0x000A),

    /**
     * Autowave study
     */
    Autowave(0x000B),

    /**
     * Weekly Pivot
     */
    WeeklyPivot(0x000F),

    /**
     * Daily Pivot
     */
    DailyPivot(0x0011),

    /**
     * Parabolic SAR
     */
    ParabolicSAR(0x0012),

    //
    // Lower Studies
    /**
     * Volume study
     */
    Volume(0x01F5),

    /**
     * Open Interest
     */
    OpenInterest(0x01F6),

    /**
     * Stochastic study
     */
    Stochastic(0x01F7),

    /**
     * MACD
     */
    MACD(0x01F8),

    /**
     * Relative Strength Index
     */
    RSI(0x01F9),

    /**
     * Momentum
     */
    Momentum(0x01FA),

    /**
     * ROC
     */
    ROC(0x01FB),

    /**
     * On Balance Volume
     */
    OnBalanceVolume(0x01FC),

    /**
     * Directional Indicator
     */
    DirectionalIndicator(0x01FD),

    /**
     * Choppiness
     */
    Choppiness(0x01FE),

    /**
     * Money Flow Index
     */
    MoneyFlowIndex(0x01FF),

    /**
     * Williams %R
     */
    WilliamsPctR(0x0201),

    /**
     * Stochastic RSI
     */
    StochRSI(0x0202),

    /**
     * Average Tune Range
     */
    AverageTuneRange(0x0203),

    /**
     * Accumulation Distribution
     */
    AccumulationDistribution(0x0205),

    /**
     * Commodity Channel Index
     */
    CommodityChannelIndex(0x0206),

    /**
     * % Price Oscillator
     */
    PercentPriceOscillator(0x0207);

    private int value;

    QStudyType(final int value)
    {
        this.value = value;
    }

    /**
     * Returns the QCW value that represents this  study type
     * 
     * @return the value used for this study in QCW files
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Returns true if this is an "upper study", that is if it is drawn in
     * the price window.
     * 
     * @return true for upper studies, false otherwise
     */
    public boolean isUpperStudy()
    {
        switch (this)
        {
        case MovingAverage:
        case BollingerBands:
        case Envelope:
        case DonchianChannel:
        case OverlayExpression:
        case KeltnerChannel:
        case FloatTurnoverChannel:
        case VolumeAtPrice:
        case Autowave:
        case WeeklyPivot:
        case DailyPivot:
        case ParabolicSAR:
            return true;

        case Volume:
        case OpenInterest:
        case Stochastic:
        case MACD:
        case RSI:
        case Momentum:
        case ROC:
        case OnBalanceVolume:
        case DirectionalIndicator:
        case Choppiness:
        case MoneyFlowIndex:
        case WilliamsPctR:
        case StochRSI:
        case AverageTuneRange:
        case AccumulationDistribution:
        case CommodityChannelIndex:
        case PercentPriceOscillator:
            return false;

        default:
            throw new QCWException("Unknown Study Type: 0x"
                + Integer.toHexString(value));
        }
    }

    /**
     * Returns the study type represented by the specified value.
     * 
     * @param value the value for which to determine study type
     * @return the study type represented by {@code value}
     */
    public static QStudyType forValue(final int value)
    {
        for (final QStudyType sType : values())
        {
            if (sType.getValue() == value)
            {
                return sType;
            }
        }

        throw new QCWException("Unknown Study Type: 0x"
            + Integer.toHexString(value));
    }

}
