package com.roberto.automatizacion.listeners;

import com.roberto.automatizacion.utilidades.UtilidadesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;

/**
 * Listener base para TestNG
 */
public class ListenerTestNG implements ITestListener {
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("=== INICIANDO SUITE: " + context.getName() + " ===");
        System.out.println("Total de pruebas a ejecutar: " + context.getAllTestMethods().length);
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("🧪 Iniciando: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ ÉXITO: " + result.getMethod().getMethodName() + 
                          " (" + (result.getEndMillis() - result.getStartMillis()) + "ms)");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String nombrePrueba = result.getMethod().getMethodName();
        System.out.println("❌ FALLO: " + nombrePrueba);
        System.out.println("   Error: " + result.getThrowable().getMessage());
        
        // Tomar screenshot en caso de fallo
        try {
            UtilidadesScreenshot.tomarScreenshot(nombrePrueba + "_FALLO");
        } catch (Exception e) {
            System.err.println("Error tomando screenshot: " + e.getMessage());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭️ OMITIDA: " + result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            System.out.println("   Razón: " + result.getThrowable().getMessage());
        }
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("=== FINALIZANDO SUITE: " + context.getName() + " ===");
        System.out.println("✅ Exitosas: " + context.getPassedTests().size());
        System.out.println("❌ Fallidas: " + context.getFailedTests().size());
        System.out.println("⏭️ Omitidas: " + context.getSkippedTests().size());
        System.out.println("⏱️ Duración total: " + 
                          (context.getEndDate().getTime() - context.getStartDate().getTime()) + "ms");
    }
}
