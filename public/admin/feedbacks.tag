<feedbacks>
	<div class="container-fluid">
		<div class="card">
		<div class="card-block">	
			<h3 class="card-title">{opts.title}</h3>
			<table class="table table-striped table-bordered table-hover table-condensed">
				<thead class="bg-info text-white">
					<tr>
						<th>Event ID</th>
						<th>Event Name</th>
						<th>Question ID</th>
						<th>Question Text</th>
						<th>Rating</th>
						<th>Comment</th>
						<th>Timestamp</th>
					</tr>
				</thead>
				<tbody>
					<tr each={ opts.feedbacks }>
						<td>{eventID}</td>
						<td>{eventName}</td>
						<td>{questionID}</td>
						<td>{questionText}</td>
						<td>{ratingAmount}</td>				
						<td>{ratingComment}</td>				
						<td>{ratingDate}</td>				
					</tr>
				</tbody>
			</table>
		</div>
		</div>
	</div>
  <!-- style -->
  <style scoped>
    h3 {
      font-size: 14px;
    }
  </style>

  <!-- logic -->
  <script>
  </script>
  
</feedbacks>