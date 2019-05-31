package Smart.EnergyManagement;

import java.util.ArrayList;
import java.util.Random;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.Histogram;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.series.Series;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.colors.XChartSeriesColors;


public class Graph {
	
	public static void draw() {
		Graph exampleChart = new Graph();
		CategoryChart chart = exampleChart.getChart();
		new SwingWrapper<CategoryChart>(chart).displayChart();
	}
 
  public CategoryChart getChart() {
 
    // Create Chart
    CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title(App.title).yAxisTitle("Power(Watt)").xAxisTitle("Time Intervals").build();
 
    // Customize Chart
    chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
    chart.getStyler().setAvailableSpaceFill(.97);//Bar kalinligi
    chart.getStyler().setOverlapped(false);//Iki bar ustuste binerken sadece true kullanılıyor
    chart.getStyler().setPlotContentSize(0.98);//Ekranın ne kadari grafikle doldurulacagi belirtiliyor
    chart.getStyler().setXAxisLabelRotation(90);//x eksenindeki yazilari 90 derece donduruyor    
    
    // Series
    
    //Grafik goruntu ayarini yapiyoruz
    Series first_series = chart.addSeries("First Loading", Datas.getHours(), Datas.getPower_before());
    first_series.setFillColor(XChartSeriesColors.BLUE);
    
    Series second_series = chart.addSeries("After Using Battery and Shifting", Datas.getHours(), Datas.getPower());
    second_series.setFillColor(XChartSeriesColors.RED);
    return chart;
  }
 
}