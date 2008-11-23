/*
 * @(#)$Id: PredefinedFunctions.java 3619 2008-03-26 07:23:03Z yui $
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.xquery.func;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.util.collections.SoftHashMap;
import xbird.util.lang.ObjectUtils;
import xbird.xquery.XQueryConstants;
import xbird.xquery.func.accessor.BaseURI;
import xbird.xquery.func.accessor.Data;
import xbird.xquery.func.accessor.DocumentUri;
import xbird.xquery.func.accessor.FnString;
import xbird.xquery.func.accessor.Nilled;
import xbird.xquery.func.accessor.NodeName;
import xbird.xquery.func.agg.Avg;
import xbird.xquery.func.agg.Count;
import xbird.xquery.func.agg.Max;
import xbird.xquery.func.agg.Min;
import xbird.xquery.func.agg.Sum;
import xbird.xquery.func.bool.False;
import xbird.xquery.func.bool.Not;
import xbird.xquery.func.bool.True;
import xbird.xquery.func.constructor.CastingFunction;
import xbird.xquery.func.constructor.DateTime;
import xbird.xquery.func.context.CurrentDateTime;
import xbird.xquery.func.context.DefaultCollation;
import xbird.xquery.func.context.ImplicitTimezone;
import xbird.xquery.func.context.Last;
import xbird.xquery.func.context.Position;
import xbird.xquery.func.context.StaticBaseUri;
import xbird.xquery.func.doc.Doc;
import xbird.xquery.func.doc.DocAvailable;
import xbird.xquery.func.doc.FnCollection;
import xbird.xquery.func.ext.Eval;
import xbird.xquery.func.ext.Hostname;
import xbird.xquery.func.ext.RemoteEval;
import xbird.xquery.func.ext.UnionAll;
import xbird.xquery.func.ext.VirtualView;
import xbird.xquery.func.math.Abs;
import xbird.xquery.func.math.Ceiling;
import xbird.xquery.func.math.Floor;
import xbird.xquery.func.math.Round;
import xbird.xquery.func.math.RoundHalfToEven;
import xbird.xquery.func.misc.Error;
import xbird.xquery.func.misc.ResolveUri;
import xbird.xquery.func.misc.Trace;
import xbird.xquery.func.node.FnName;
import xbird.xquery.func.node.FnNumber;
import xbird.xquery.func.node.Lang;
import xbird.xquery.func.node.LocalName;
import xbird.xquery.func.node.NamespaceUri;
import xbird.xquery.func.node.Root;
import xbird.xquery.func.qname.FnQName;
import xbird.xquery.func.qname.InScopePrefixes;
import xbird.xquery.func.qname.LocalNameFromQName;
import xbird.xquery.func.qname.NamespaceUriForPrefix;
import xbird.xquery.func.qname.NamespaceUriFromQName;
import xbird.xquery.func.qname.PrefixFromQName;
import xbird.xquery.func.qname.ResolveQName;
import xbird.xquery.func.seq.DeepEqual;
import xbird.xquery.func.seq.DistinctValues;
import xbird.xquery.func.seq.Empty;
import xbird.xquery.func.seq.ExactlyOne;
import xbird.xquery.func.seq.Exists;
import xbird.xquery.func.seq.FnBoolean;
import xbird.xquery.func.seq.Id;
import xbird.xquery.func.seq.IdRef;
import xbird.xquery.func.seq.IndexOf;
import xbird.xquery.func.seq.InsertBefore;
import xbird.xquery.func.seq.OneOrMore;
import xbird.xquery.func.seq.Remove;
import xbird.xquery.func.seq.Reverse;
import xbird.xquery.func.seq.Subsequence;
import xbird.xquery.func.seq.Unordered;
import xbird.xquery.func.seq.ZeroOrOne;
import xbird.xquery.func.string.CodepointEqual;
import xbird.xquery.func.string.CodepointsToString;
import xbird.xquery.func.string.Compare;
import xbird.xquery.func.string.Concat;
import xbird.xquery.func.string.EncodeForUri;
import xbird.xquery.func.string.EscapeHtmlUri;
import xbird.xquery.func.string.IriToUri;
import xbird.xquery.func.string.LowerCase;
import xbird.xquery.func.string.Matches;
import xbird.xquery.func.string.NormalizeSpace;
import xbird.xquery.func.string.NormalizeUnicode;
import xbird.xquery.func.string.Replace;
import xbird.xquery.func.string.StringJoin;
import xbird.xquery.func.string.StringLength;
import xbird.xquery.func.string.StringToCodepoint;
import xbird.xquery.func.string.Substring;
import xbird.xquery.func.string.SubstringMatch;
import xbird.xquery.func.string.Tokenize;
import xbird.xquery.func.string.Translate;
import xbird.xquery.func.string.UpperCase;
import xbird.xquery.func.time.AdjustTimezone;
import xbird.xquery.func.time.ExtractFromDateTime;
import xbird.xquery.func.time.ExtractFromDuration;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.xs.AnyURIType;
import xbird.xquery.type.xs.Base64BinaryType;
import xbird.xquery.type.xs.BooleanType;
import xbird.xquery.type.xs.ByteType;
import xbird.xquery.type.xs.DateTimeType;
import xbird.xquery.type.xs.DateType;
import xbird.xquery.type.xs.DayTimeDurationType;
import xbird.xquery.type.xs.DecimalType;
import xbird.xquery.type.xs.DoubleType;
import xbird.xquery.type.xs.DurationType;
import xbird.xquery.type.xs.ENTITYType;
import xbird.xquery.type.xs.FloatType;
import xbird.xquery.type.xs.GDayType;
import xbird.xquery.type.xs.GMonthDayType;
import xbird.xquery.type.xs.GMonthType;
import xbird.xquery.type.xs.GYearMonthType;
import xbird.xquery.type.xs.GYearType;
import xbird.xquery.type.xs.HexBinaryType;
import xbird.xquery.type.xs.IDREFType;
import xbird.xquery.type.xs.IDType;
import xbird.xquery.type.xs.IntType;
import xbird.xquery.type.xs.IntegerType;
import xbird.xquery.type.xs.LanguageType;
import xbird.xquery.type.xs.LongType;
import xbird.xquery.type.xs.NCNameType;
import xbird.xquery.type.xs.NMTokenType;
import xbird.xquery.type.xs.NameType;
import xbird.xquery.type.xs.NegativeIntegerType;
import xbird.xquery.type.xs.NonNegativeIntegerType;
import xbird.xquery.type.xs.NonPositiveIntegerType;
import xbird.xquery.type.xs.NormalizedStringType;
import xbird.xquery.type.xs.PositiveIntegerType;
import xbird.xquery.type.xs.ShortType;
import xbird.xquery.type.xs.StringType;
import xbird.xquery.type.xs.TimeType;
import xbird.xquery.type.xs.TokenType;
import xbird.xquery.type.xs.UnsignedByteType;
import xbird.xquery.type.xs.UnsignedIntType;
import xbird.xquery.type.xs.UnsignedLongType;
import xbird.xquery.type.xs.UnsignedShortType;
import xbird.xquery.type.xs.UntypedAtomicType;
import xbird.xquery.type.xs.YearMonthDurationType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PredefinedFunctions {

    /** func name, func pair [soft V] */
    private static final Map<String, BuiltInFunction> table = new SoftHashMap<String, BuiltInFunction>();

    /** func name, class pair [hard K,V] */
    private static final Map<String, String> resolver;

    static {
        final Map<String, String> r = new HashMap<String, String>();
        //2 Accessors
        r.put(NodeName.SYMBOL, NodeName.class.getName());
        r.put(Nilled.SYMBOL, Nilled.class.getName());
        r.put(FnString.SYMBOL, FnString.class.getName());
        r.put(Data.SYMBOL, Data.class.getName());
        r.put(BaseURI.SYMBOL, BaseURI.class.getName());
        r.put(DocumentUri.SYMBOL, DocumentUri.class.getName());
        //3 The Error Function
        r.put(Error.SYMBOL, Error.class.getName());
        //4 The Trace Function
        r.put(Trace.SYMBOL, Trace.class.getName());
        //5 Constructor Functions
        r.put(DateTime.SYMBOL, DateTime.class.getName());
        r.put(StringType.SYMBOL, CastingFunction.CLAZZ);
        r.put(BooleanType.SYMBOL, CastingFunction.CLAZZ);
        r.put(DecimalType.SYMBOL, CastingFunction.CLAZZ);
        r.put(FloatType.SYMBOL, CastingFunction.CLAZZ);
        r.put(DoubleType.SYMBOL, CastingFunction.CLAZZ);
        r.put(DurationType.SYMBOL, CastingFunction.CLAZZ);
        r.put(DateTimeType.SYMBOL, CastingFunction.CLAZZ);
        r.put(TimeType.SYMBOL, CastingFunction.CLAZZ);
        r.put(DateType.SYMBOL, CastingFunction.CLAZZ);
        r.put(GYearType.SYMBOL, CastingFunction.CLAZZ);
        r.put(GYearMonthType.SYMBOL, CastingFunction.CLAZZ);
        r.put(GDayType.SYMBOL, CastingFunction.CLAZZ);
        r.put(GMonthType.SYMBOL, CastingFunction.CLAZZ);
        r.put(GMonthDayType.SYMBOL, CastingFunction.CLAZZ);
        r.put(HexBinaryType.SYMBOL, CastingFunction.CLAZZ);
        r.put(Base64BinaryType.SYMBOL, CastingFunction.CLAZZ);
        r.put(AnyURIType.SYMBOL, CastingFunction.CLAZZ);
        r.put(CastingFunction.XsQName.SYMBOL, CastingFunction.CLAZZ);
        r.put(NormalizedStringType.SYMBOL, CastingFunction.CLAZZ);
        r.put(TokenType.SYMBOL, CastingFunction.CLAZZ);
        r.put(LanguageType.SYMBOL, CastingFunction.CLAZZ);
        r.put(NMTokenType.SYMBOL, CastingFunction.CLAZZ);
        r.put(NameType.SYMBOL, CastingFunction.CLAZZ);
        r.put(NCNameType.SYMBOL, CastingFunction.CLAZZ);
        r.put(IDType.SYMBOL, CastingFunction.CLAZZ);
        r.put(IDREFType.SYMBOL, CastingFunction.CLAZZ);
        r.put(ENTITYType.SYMBOL, CastingFunction.CLAZZ);
        r.put(IntegerType.SYMBOL, CastingFunction.CLAZZ);
        r.put(NonPositiveIntegerType.SYMBOL, CastingFunction.CLAZZ);
        r.put(NegativeIntegerType.SYMBOL, CastingFunction.CLAZZ);
        r.put(LongType.SYMBOL, CastingFunction.CLAZZ);
        r.put(IntType.SYMBOL, CastingFunction.CLAZZ);
        r.put(ShortType.SYMBOL, CastingFunction.CLAZZ);
        r.put(ByteType.SYMBOL, CastingFunction.CLAZZ);
        r.put(NonNegativeIntegerType.SYMBOL, CastingFunction.CLAZZ);
        r.put(UnsignedLongType.SYMBOL, CastingFunction.CLAZZ);
        r.put(UnsignedIntType.SYMBOL, CastingFunction.CLAZZ);
        r.put(UnsignedShortType.SYMBOL, CastingFunction.CLAZZ);
        r.put(UnsignedByteType.SYMBOL, CastingFunction.CLAZZ);
        r.put(PositiveIntegerType.SYMBOL, CastingFunction.CLAZZ);
        r.put(YearMonthDurationType.SYMBOL, CastingFunction.CLAZZ);
        r.put(DayTimeDurationType.SYMBOL, CastingFunction.CLAZZ);
        r.put(UntypedAtomicType.SYMBOL, CastingFunction.CLAZZ);
        //6 Functions and Operators on Numerics
        r.put(Abs.SYMBOL, Abs.class.getName());
        r.put(Ceiling.SYMBOL, Ceiling.class.getName());
        r.put(Floor.SYMBOL, Floor.class.getName());
        r.put(Round.SYMBOL, Round.class.getName());
        r.put(RoundHalfToEven.SYMBOL, RoundHalfToEven.class.getName());
        //7 Functions on Strings
        r.put(CodepointsToString.SYMBOL, CodepointsToString.class.getName());
        r.put(StringToCodepoint.SYMBOL, StringToCodepoint.class.getName());
        r.put(Compare.SYMBOL, Compare.class.getName());
        r.put(CodepointEqual.SYMBOL, CodepointEqual.class.getName());
        r.put(Concat.SYMBOL, Concat.class.getName());
        r.put(StringJoin.SYMBOL, StringJoin.class.getName());
        r.put(Substring.SYMBOL, Substring.class.getName());
        r.put(StringLength.SYMBOL, StringLength.class.getName());
        r.put(NormalizeSpace.SYMBOL, NormalizeSpace.class.getName());
        r.put(NormalizeUnicode.SYMBOL, NormalizeUnicode.class.getName());
        r.put(UpperCase.SYMBOL, UpperCase.class.getName());
        r.put(LowerCase.SYMBOL, LowerCase.class.getName());
        r.put(Translate.SYMBOL, Translate.class.getName());
        r.put(EncodeForUri.SYMBOL, EncodeForUri.class.getName());
        r.put(IriToUri.SYMBOL, IriToUri.class.getName());
        r.put(EscapeHtmlUri.SYMBOL, EscapeHtmlUri.class.getName());
        r.put(SubstringMatch.Contains.SYMBOL, SubstringMatch.Contains.class.getName());
        r.put(SubstringMatch.StartWith.SYMBOL, SubstringMatch.StartWith.class.getName());
        r.put(SubstringMatch.EndWith.SYMBOL, SubstringMatch.EndWith.class.getName());
        r.put(SubstringMatch.SubstringBefore.SYMBOL, SubstringMatch.SubstringBefore.class.getName());
        r.put(SubstringMatch.SubstringAfter.SYMBOL, SubstringMatch.SubstringAfter.class.getName());
        r.put(Matches.SYMBOL, Matches.class.getName());
        r.put(Replace.SYMBOL, Replace.class.getName());
        r.put(Tokenize.SYMBOL, Tokenize.class.getName());
        //8 Functions and Operators for anyURI
        r.put(ResolveUri.SYMBOL, ResolveUri.class.getName());
        //9 Functions and Operators on Boolean Values
        r.put(True.SYMBOL, True.class.getName());
        r.put(False.SYMBOL, False.class.getName());
        r.put(Not.SYMBOL, Not.class.getName());
        //10 Functions and Operators on Durations, Dates and Times
        r.put(ExtractFromDuration.YearsFromDuration.SYMBOL, ExtractFromDuration.YearsFromDuration.class.getName());
        r.put(ExtractFromDuration.MonthsFromDuration.SYMBOL, ExtractFromDuration.MonthsFromDuration.class.getName());
        r.put(ExtractFromDuration.DaysFromDuration.SYMBOL, ExtractFromDuration.DaysFromDuration.class.getName());
        r.put(ExtractFromDuration.HoursFromDuration.SYMBOL, ExtractFromDuration.HoursFromDuration.class.getName());
        r.put(ExtractFromDuration.MinutesFromDuration.SYMBOL, ExtractFromDuration.MinutesFromDuration.class.getName());
        r.put(ExtractFromDuration.SecondsFromDuration.SYMBOL, ExtractFromDuration.SecondsFromDuration.class.getName());
        r.put(ExtractFromDateTime.YearFromDateTime.SYMBOL, ExtractFromDateTime.YearFromDateTime.class.getName());
        r.put(ExtractFromDateTime.MonthFromDateTime.SYMBOL, ExtractFromDateTime.MonthFromDateTime.class.getName());
        r.put(ExtractFromDateTime.DayFromDateTime.SYMBOL, ExtractFromDateTime.DayFromDateTime.class.getName());
        r.put(ExtractFromDateTime.HoursFromDateTime.SYMBOL, ExtractFromDateTime.HoursFromDateTime.class.getName());
        r.put(ExtractFromDateTime.MinutesFromDateTime.SYMBOL, ExtractFromDateTime.MinutesFromDateTime.class.getName());
        r.put(ExtractFromDateTime.SecondsFromDateTime.SYMBOL, ExtractFromDateTime.SecondsFromDateTime.class.getName());
        r.put(ExtractFromDateTime.TimezoneFromDateTime.SYMBOL, ExtractFromDateTime.TimezoneFromDateTime.class.getName());
        r.put(ExtractFromDateTime.YearFromDate.SYMBOL, ExtractFromDateTime.YearFromDate.class.getName());
        r.put(ExtractFromDateTime.MonthFromDate.SYMBOL, ExtractFromDateTime.MonthFromDate.class.getName());
        r.put(ExtractFromDateTime.DayFromDate.SYMBOL, ExtractFromDateTime.DayFromDate.class.getName());
        r.put(ExtractFromDateTime.TimezoneFromDate.SYMBOL, ExtractFromDateTime.TimezoneFromDate.class.getName());
        r.put(ExtractFromDateTime.HoursFromTime.SYMBOL, ExtractFromDateTime.HoursFromTime.class.getName());
        r.put(ExtractFromDateTime.MinutesFromTime.SYMBOL, ExtractFromDateTime.MinutesFromTime.class.getName());
        r.put(ExtractFromDateTime.SecondsFromTime.SYMBOL, ExtractFromDateTime.SecondsFromTime.class.getName());
        r.put(ExtractFromDateTime.TimezoneFromTime.SYMBOL, ExtractFromDateTime.TimezoneFromTime.class.getName());
        r.put(AdjustTimezone.AdjustDateTimeToTimezone.SYMBOL, AdjustTimezone.AdjustDateTimeToTimezone.class.getName());
        r.put(AdjustTimezone.AdjustDateToTimezone.SYMBOL, AdjustTimezone.AdjustDateToTimezone.class.getName());
        r.put(AdjustTimezone.AdjustTimeToTimezone.SYMBOL, AdjustTimezone.AdjustTimeToTimezone.class.getName());
        //11 Functions Related to QNames
        r.put(ResolveQName.SYMBOL, ResolveQName.class.getName());
        r.put(FnQName.SYMBOL, FnQName.class.getName());
        r.put(PrefixFromQName.SYMBOL, PrefixFromQName.class.getName());
        r.put(LocalNameFromQName.SYMBOL, LocalNameFromQName.class.getName());
        r.put(NamespaceUriFromQName.SYMBOL, NamespaceUriFromQName.class.getName());
        r.put(NamespaceUriForPrefix.SYMBOL, NamespaceUriForPrefix.class.getName());
        r.put(InScopePrefixes.SYMBOL, InScopePrefixes.class.getName());
        //14 Functions and Operators on Nodes
        r.put(FnName.SYMBOL, FnName.class.getName());
        r.put(LocalName.SYMBOL, LocalName.class.getName());
        r.put(NamespaceUri.SYMBOL, NamespaceUri.class.getName());
        r.put(FnNumber.SYMBOL, FnNumber.class.getName());
        r.put(Lang.SYMBOL, Lang.class.getName());
        r.put(Root.SYMBOL, Root.class.getName());
        //15 Functions and Operators on Sequences
        r.put(FnBoolean.SYMBOL, FnBoolean.class.getName());
        r.put(IndexOf.SYMBOL, IndexOf.class.getName());
        r.put(Empty.SYMBOL, Empty.class.getName());
        r.put(Exists.SYMBOL, Exists.class.getName());
        r.put(DistinctValues.SYMBOL, DistinctValues.class.getName());
        r.put(InsertBefore.SYMBOL, InsertBefore.class.getName());
        r.put(Remove.SYMBOL, Remove.class.getName());
        r.put(Reverse.SYMBOL, Reverse.class.getName());
        r.put(Subsequence.SYMBOL, Subsequence.class.getName());
        r.put(Unordered.SYMBOL, Unordered.class.getName());
        r.put(ZeroOrOne.SYMBOL, ZeroOrOne.class.getName());
        r.put(OneOrMore.SYMBOL, OneOrMore.class.getName());
        r.put(ExactlyOne.SYMBOL, ExactlyOne.class.getName());
        r.put(DeepEqual.SYMBOL, DeepEqual.class.getName());
        r.put(Count.SYMBOL, Count.class.getName());
        r.put(Avg.SYMBOL, Avg.class.getName());
        r.put(Max.SYMBOL, Max.class.getName());
        r.put(Min.SYMBOL, Min.class.getName());
        r.put(Sum.SYMBOL, Sum.class.getName());
        r.put(Id.SYMBOL, Id.class.getName());
        r.put(IdRef.SYMBOL, IdRef.class.getName());
        r.put(Doc.SYMBOL, Doc.class.getName());
        r.put(DocAvailable.SYMBOL, DocAvailable.class.getName());
        r.put(FnCollection.SYMBOL, FnCollection.class.getName());
        //16 Context Functions
        r.put(Position.SYMBOL, Position.class.getName());
        r.put(Last.SYMBOL, Last.class.getName());
        r.put(CurrentDateTime.SYMBOL, CurrentDateTime.class.getName());
        r.put(CurrentDateTime.CurrentDate.SYMBOL, CurrentDateTime.CurrentDate.class.getName());
        r.put(CurrentDateTime.CurrentTime.SYMBOL, CurrentDateTime.CurrentTime.class.getName());
        r.put(ImplicitTimezone.SYMBOL, ImplicitTimezone.class.getName());
        r.put(DefaultCollation.SYMBOL, DefaultCollation.class.getName());
        r.put(StaticBaseUri.SYMBOL, StaticBaseUri.class.getName());
        //extended functions
        r.put(Eval.SYMBOL, Eval.class.getName());
        r.put(RemoteEval.SYMBOL, RemoteEval.class.getName());
        r.put(VirtualView.SYMBOL, VirtualView.class.getName());
        r.put(UnionAll.SYMBOL, UnionAll.class.getName());
        r.put(Hostname.SYMBOL, Hostname.class.getName());

        activateUserExtentions(r);

        resolver = r;
    }

    /**
     * Restricts instantiations.
     */
    private PredefinedFunctions() {}

    public static BuiltInFunction lookup(QualifiedName name) {
        String uri = name.getNamespaceURI();
        final String prefix;
        if(XQueryConstants.FN_URI.equals(uri)) {
            prefix = XQueryConstants.FN;
        } else if(XQueryConstants.XS_URI.equals(uri)) {
            prefix = XQueryConstants.XS;
        } else if(XQueryConstants.XDT_URI.equals(uri)) {
            prefix = XQueryConstants.XDT;
        } else if(BuiltInFunction.EXT_NAMESPACE_URI.equals(uri)) {
            prefix = BuiltInFunction.EXT_NSPREFIX;
        } else {
            return null;
        }
        String fname = prefix + ':' + name.getLocalPart();
        return lookup(fname);
    }

    public static BuiltInFunction lookup(String fname) {
        String clazz = resolver.get(fname);
        if(clazz == null) {
            return null;
        }
        BuiltInFunction func = table.get(fname);
        return (func == null) ? reborn(clazz, fname) : func;
    }

    private static BuiltInFunction reborn(String clazz, String fname) {
        final BuiltInFunction f;
        if(ObjectUtils.hasDefaultConstructor(clazz)) {
            f = ObjectUtils.<BuiltInFunction> instantiate(clazz);
        } else {
            f = ObjectUtils.<BuiltInFunction> instantiate(clazz, new Class[] { String.class }, fname);
        }
        if(clazz == CastingFunction.CLAZZ) {
            CastingFunction castFunc = (CastingFunction) f;
            castFunc.initialize(fname);
        }
        if(f.isReusable()) {
            table.put(fname, f);
        }
        return f;
    }

    private static void activateUserExtentions(final Map<String, String> r) {
        final String providerClazz = Settings.get("xbird.xquery.func.provider");
        if(providerClazz != null && providerClazz.length() > 0) {
            Object obj = ObjectUtils.instantiateSafely(providerClazz);
            if(obj != null && (obj instanceof FunctionProvider)) {
                FunctionProvider provider = (FunctionProvider) obj;
                final List<? extends BuiltInFunction> funcs = provider.injectedFunctions();
                if(funcs != null) {
                    final Log LOG = LogFactory.getLog(PredefinedFunctions.class);
                    for(BuiltInFunction f : funcs) {
                        QualifiedName qname = f.getName();
                        String name = QNameUtil.toLexicalForm(qname);
                        String prefix = qname.getPrefix();
                        if(BuiltInFunction.EXT_NSPREFIX.equals(prefix)) {
                            table.put(name, f);
                            String clazzName = f.getClass().getName();
                            r.put(name, clazzName);
                        } else {
                            LOG.warn("loading a BuiltInFunction is discarded: " + name);
                        }

                    }
                }
            } else {
                Log LOG = LogFactory.getLog(PredefinedFunctions.class);
                LOG.warn("Illegal FunctionProvider: " + providerClazz);
            }
        }
    }

}
