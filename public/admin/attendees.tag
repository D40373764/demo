<attendees>
	<div class="container-fluid">
	<div class="card">
	<div class="card-block">
		<h3 class="card-title">{opts.title}</h3>
		<table class="table table-striped table-bordered table-hover table-condensed">
			<thead class="bg-info text-white">
				<tr>
					<th>Event ID</th>
					<th>Event Name</th>
					<th>Attendee ID</th>
					<th>first Name</th>
					<th>Last Name</th>
				</tr>
			</thead>
			<tbody>
				<tr each={ opts.attendees }>
					<td>{eventID}</td>
					<td>{eventName}</td>
					<td>{attendeeID}</td>				
					<td>{firstName}</td>
					<td>{lastName}</td>
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
  
</attendees>