package com.nammamistri.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Site(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val clientName: String = "",
    val startDate: String = "",
    val isActive: Boolean = true
)

data class Worker(
    val id: String = "",
    val siteId: String = "",
    val name: String = "",
    val dailyWage: Double = 0.0
)

data class WorkLog(
    val id: String = "",
    val workerId: String = "",
    val date: String = "",
    val daysWorked: Double = 0.0,
    val advancePaid: Double = 0.0,
    val note: String = ""
)

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun userId() = auth.currentUser?.uid ?: throw Exception("Not logged in")

    private fun sitesCol() = db.collection("users").document(userId()).collection("sites")
    private fun workersCol() = db.collection("users").document(userId()).collection("workers")
    private fun logsCol() = db.collection("users").document(userId()).collection("logs")

    // Sites
    suspend fun getSites(): List<Site> {
        return sitesCol().get().await().documents.map { doc ->
            Site(
                id = doc.id,
                name = doc.getString("name") ?: "",
                location = doc.getString("location") ?: "",
                clientName = doc.getString("clientName") ?: "",
                startDate = doc.getString("startDate") ?: "",
                isActive = doc.getBoolean("isActive") ?: true
            )
        }
    }

    suspend fun addSite(site: Site): String {
        val data = hashMapOf(
            "name" to site.name,
            "location" to site.location,
            "clientName" to site.clientName,
            "startDate" to site.startDate,
            "isActive" to site.isActive
        )
        val ref = sitesCol().add(data).await()
        return ref.id
    }

    suspend fun deleteSite(siteId: String) {
        sitesCol().document(siteId).delete().await()
        // Delete all workers and logs for this site
        val workers = workersCol().whereEqualTo("siteId", siteId).get().await()
        for (worker in workers.documents) {
            deleteWorker(worker.id)
        }
    }

    // Workers
    suspend fun getWorkers(siteId: String): List<Worker> {
        return workersCol().whereEqualTo("siteId", siteId).get().await().documents.map { doc ->
            Worker(
                id = doc.id,
                siteId = doc.getString("siteId") ?: "",
                name = doc.getString("name") ?: "",
                dailyWage = doc.getDouble("dailyWage") ?: 0.0
            )
        }
    }

    suspend fun addWorker(worker: Worker): String {
        val data = hashMapOf(
            "siteId" to worker.siteId,
            "name" to worker.name,
            "dailyWage" to worker.dailyWage
        )
        val ref = workersCol().add(data).await()
        return ref.id
    }

    suspend fun deleteWorker(workerId: String) {
        workersCol().document(workerId).delete().await()
        val logs = logsCol().whereEqualTo("workerId", workerId).get().await()
        for (log in logs.documents) {
            logsCol().document(log.id).delete().await()
        }
    }

    // Logs
    suspend fun getLogs(workerId: String): List<WorkLog> {
        return logsCol().whereEqualTo("workerId", workerId).get().await().documents.map { doc ->
            WorkLog(
                id = doc.id,
                workerId = doc.getString("workerId") ?: "",
                date = doc.getString("date") ?: "",
                daysWorked = doc.getDouble("daysWorked") ?: 0.0,
                advancePaid = doc.getDouble("advancePaid") ?: 0.0,
                note = doc.getString("note") ?: ""
            )
        }
    }

    suspend fun addLog(log: WorkLog) {
        val data = hashMapOf(
            "workerId" to log.workerId,
            "date" to log.date,
            "daysWorked" to log.daysWorked,
            "advancePaid" to log.advancePaid,
            "note" to log.note
        )
        logsCol().add(data).await()
    }

    suspend fun getTotalDaysWorked(workerId: String): Double {
        val logs = logsCol().whereEqualTo("workerId", workerId).get().await()
        return logs.documents.sumOf { it.getDouble("daysWorked") ?: 0.0 }
    }

    suspend fun getTotalAdvancePaid(workerId: String): Double {
        val logs = logsCol().whereEqualTo("workerId", workerId).get().await()
        return logs.documents.sumOf { it.getDouble("advancePaid") ?: 0.0 }
    }
}
